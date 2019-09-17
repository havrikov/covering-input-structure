#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import abc
import csv
import json
import logging
import os
import re
import subprocess
import sys
from collections import defaultdict, Counter
from functools import reduce
from pathlib import Path
from typing import List, Dict

import luigi
import nbformat as nbf
import pandas as pd
from nbconvert import HTMLExporter
from nbconvert.preprocessors import ExecutePreprocessor


class ExperimentConfig(luigi.Config):
    experiment_dir: str = luigi.Parameter(description="The path to where all the experiments happen. (like ../k-path-experiments)")
    tree_depth: int = luigi.IntParameter(description="The maximum tree generation depth")
    drivers: Dict[str, Dict] = luigi.DictParameter(description="Test subject configuration", default={
        "json": {
            "suffix": ".json",
            "tribble_grammar": "grammars/tribble/json.scala",
            "antlr_grammar": "grammars/antlr/JSON.g4",
            "drivers": {
                "argo": "argo-5.4",
                "fastjson": "fastjson-1.2.51",
                "genson": "genson-1.4",
                "gson": "gson-2.8.5",
                "jackson-databind": "jackson-databind-2.9.8",
                "json-flattener": "json-flattener-0.6.0",
                "json-java": "json-20180813",
                "json-simple": "json-simple-1.1.1",
                "json-simple-cliftonlabs": "json-simple-3.0.2",
                "minimal-json": "minimal-json-0.9.5",
                "pojo": "jsonschema2pojo-core-1.0.0",
            },
        },
        "csv": {
            "suffix": ".csv",
            "tribble_grammar": "grammars/tribble/csv.scala",
            "antlr_grammar": "grammars/antlr/CSV.g4",
            "drivers": {
                "commons-csv": "commons-csv-1.6",
                "jackson-dataformat-csv": "jackson-dataformat-csv-2.9.8",
                "jcsv": "jcsv-1.4.0",
                "sfm-csv": "sfm-csv-6.1.1",
                "simplecsv": "simplecsv-2.1",
                "super-csv": "super-csv-2.4.0",
            }
        },
        "url": {
            "suffix": ".txt",
            "tribble_grammar": "grammars/tribble/url.scala",
            "antlr_grammar": "grammars/antlr/url.g4",
            "drivers": {
                "autolink": "autolink-0.9.0",
                "galimatias": "galimatias-0.2.1",
                "jurl": "jurl-v0.3.0",
                "url-detector": "url-detector-0.1.17",
            }
        },
        "markdown": {
            "suffix": ".md",
            "tribble_grammar": "grammars/tribble/markdown.scala",
            "antlr_grammar": "grammars/antlr/Markdown.g4",
            "drivers": {
                "commonmark": "commonmark-0.11.0",
                "markdown4j": "markdown4j-2.2-cj-1.1",
                "txtmark": "txtmark-0.13",
            }
        }
    })


def root_dir() -> Path:
    return Path(ExperimentConfig().experiment_dir)


def depth() -> str:
    return str(ExperimentConfig().tree_depth)


class BuildGrammarinatorProducer(luigi.Task):
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    task_namespace = "prerequisites"

    def output(self):
        return luigi.LocalTarget(str(Path("tools") / "grammarinator" / self.format))

    def run(self):
        grammar = ExperimentConfig().drivers[self.format]["antlr_grammar"]
        with self.output().temporary_path() as out:
            subprocess.run(["grammarinator-process", "--no-actions", grammar, "-o", out], check=True)


class BuildTribble(luigi.Task):
    task_namespace = "prerequisites"

    def output(self):
        return luigi.LocalTarget(str(Path("tools") / "tribble" / "build" / "libs" / "tribble-0.1.jar"))

    def run(self):
        subprocess.run(["./gradlew", "assemble"], check=True, cwd=Path("tools") / "tribble")


class BuildSubject(luigi.Task):
    subject_name: str = luigi.Parameter(description="The name of the subject to build")
    task_namespace = "prerequisites"

    def output(self):
        return luigi.LocalTarget(str(Path("tools") / "subjects" / self.subject_name / "build" / "libs" / f"{self.subject_name}-subject.jar"))

    def run(self):
        subprocess.run(["../gradlew", "build"], check=True, cwd=Path("tools") / "subjects" / self.subject_name)


class DownloadOriginalBytecode(luigi.Task):
    subject_name: str = luigi.Parameter(description="The name of the subject to build")
    subject_original_artifact: str = luigi.Parameter(description="The name of the original bytecode artifact of the subject")
    task_namespace = "prerequisites"

    def output(self):
        return luigi.LocalTarget(
            str(Path("tools") / "subjects" / self.subject_name / "build" / "original" / f"{self.subject_original_artifact}.jar"))

    def run(self):
        subprocess.run(["../gradlew", "downloadOriginalJar"], check=True, cwd=Path("tools") / "subjects" / self.subject_name)


class KPath(object):
    k: int = luigi.IntParameter(description="The k for the k-path coverage")

    def tool(self) -> str:
        return f"{self.k}-path"


class Grammarinator(KPath):
    def tool(self) -> str:
        return f"{self.k}-grammarinator"


class GenerateKPath(KPath, luigi.Task):
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    run_num: int = luigi.IntParameter(description="The number of this run", positional=False)
    task_namespace = 'generation'
    resources = {'ram': 4}

    def requires(self):
        return BuildTribble()

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "generated-inputs" / self.format / self.tool() / f"run{self.run_num}"))

    def run(self):
        driver_info = ExperimentConfig().drivers[self.format]
        with self.output().temporary_path() as out:
            args = ["java",
                    "-Xss100m",
                    "-Xms256m",
                    f'-Xmx{self.resources["ram"]}g',
                    "-server",
                    "-XX:ParallelGCThreads=2",
                    "-XX:CICompilerCount=2",
                    "-jar",
                    self.input().path,
                    f'--automaton-dir={str(root_dir() / "automaton-cache" / f"{self.k}" / f"run{self.run_num}")}',
                    "generate",
                    f'--suffix={driver_info["suffix"]}',
                    f"--out-dir={out}",
                    f'--grammar-file={driver_info["tribble_grammar"]}',
                    f"--mode={self.k}-path-{depth()}",
                    f"--heuristic={self.k}-path-coverage",
                    "--unfold-regexes",
                    "--merge-literals"
                    ]
            logging.info('Launching %s', ' '.join(args))
            subprocess.run(args, check=True, stdout=subprocess.DEVNULL)


class RunGrammarinator(Grammarinator, luigi.Task):
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    run_num: int = luigi.IntParameter(description="The number of this run", positional=False)
    task_namespace = 'generation'
    resources = {'ram': 4}

    def requires(self):
        return {"fairness": FairnessParameters(format=self.format, k=self.k, run_num=self.run_num),
                "producer": BuildGrammarinatorProducer(format=self.format)}

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "generated-inputs" / self.format / self.tool() / f"run{self.run_num}"))

    def run(self):
        driver_info = ExperimentConfig().drivers[self.format]
        with self.input()["fairness"].open("r") as f:
            row = next(csv.DictReader(f))
            num_files = row["num_files"]
        producer_dir = Path(self.input()["producer"].path)
        grammar_name = Path(driver_info["antlr_grammar"]).stem
        with self.output().temporary_path() as out:
            args = ["grammarinator-generate",
                    "-l", str(producer_dir / f"{grammar_name}Unlexer.py"),
                    "-p", str(producer_dir / f"{grammar_name}Unparser.py"),
                    "-n", num_files,
                    "-o", str(Path(out) / f"file%d{driver_info['suffix']}"),
                    "-d", depth(),
                    "-c", "0.9",
                    "-t", "grammarinator.runtime.simple_space_transformer"
                    ]
            logging.debug("Launching %s", " ".join(args))
            subprocess.run(args, check=True, stdout=subprocess.DEVNULL)


class FairnessParameters(luigi.Task):
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    run_num: int = luigi.IntParameter(description="The number of this run", positional=False)
    k: int = luigi.IntParameter(description="The k for the corresponding k-path algorithm", positional=False)
    task_namespace = "meta-data"
    regex = re.compile("_(?P<size>\\d+)_(?P<depth>\\d+)_")

    def requires(self):
        return GenerateKPath(format=self.format, run_num=self.run_num, k=self.k)

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "input-metadata" / self.format / f"k{self.k}" / f"run{self.run_num}.csv"))

    def run(self):
        driver_info = ExperimentConfig().drivers[self.format]
        min_size = sys.maxsize
        agr_size = 0
        max_size = 0
        max_depth = 0
        num_files = 0
        for run_dir in self.deps():
            file_names = [f for f in os.listdir(run_dir.output().path) if f.startswith("file") and f.endswith(driver_info["suffix"])]
            num_files += len(file_names)
            for file_name in file_names:
                match = re.search(self.regex, file_name)
                if match:
                    size = int(match.group("size"))
                    agr_size += size
                    if size < min_size:
                        min_size = size
                    if size > max_size:
                        max_size = size
                    depth = int(match.group("depth"))
                    if max_depth < depth:
                        max_depth = depth
        with self.output().open('w') as f:
            f.write(f'num_files,min_size,avg_size,max_size,max_depth,grammar\n{num_files},{min_size},{agr_size // num_files},{max_size},{max_depth},{driver_info["tribble_grammar"]}')


class RunDriver(luigi.Task):
    __metaclass__ = abc.ABCMeta
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    driver_name: str = luigi.Parameter(description="The driver name", positional=False)
    run_num: int = luigi.IntParameter(description="The number of this run", positional=False)
    task_namespace = 'evaluation'
    resources = {'ram': 1}

    @abc.abstractmethod
    def tool(self) -> str:
        return ""

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "evaluation" / self.format / self.driver_name / "runs" / self.tool() / f"run{self.run_num}.csv"))

    def run(self):
        with self.output().temporary_path() as res:
            args = ["java",
                    "-Xss10m",
                    "-Xms256m",
                    f'-Xmx{self.resources["ram"]}g',
                    "-server",
                    "-XX:ParallelGCThreads=2",
                    "-XX:CICompilerCount=2",
                    "-jar",
                    self.input()["subject"].path,
                    "--ignore-exceptions",
                    "--log-exceptions",
                    str(Path(res).with_suffix(".exceptions.json")),
                    "--report-coverage",
                    res,
                    "--original-bytecode",
                    self.input()["original"].path,
                    self.input()["inputs"].path
                    ]
            logging.info('Launching %s', ' '.join(args))
            subprocess.run(args, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


class ExecuteKRun(KPath, RunDriver):
    def requires(self):
        original_name = ExperimentConfig().drivers[self.format]["drivers"][self.driver_name]
        return {"subject": BuildSubject(self.driver_name),
                "original": DownloadOriginalBytecode(self.driver_name, original_name),
                "inputs": GenerateKPath(format=self.format, run_num=self.run_num, k=self.k)}


class ExecuteGrammarinatorRun(Grammarinator, RunDriver):
    def requires(self):
        original_name = ExperimentConfig().drivers[self.format]["drivers"][self.driver_name]
        return {"subject": BuildSubject(self.driver_name),
                "original": DownloadOriginalBytecode(self.driver_name, original_name),
                "inputs": RunGrammarinator(format=self.format, run_num=self.run_num, k=self.k)}


class MergeExceptions(luigi.Task):
    __metaclass__ = abc.ABCMeta
    driver_name: str = luigi.Parameter(description="The driver name", positional=False)
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    runs: int = luigi.IntParameter(description="Number of runs to generate", positional=False)
    task_namespace = "postprocessing"

    @abc.abstractmethod
    def tool(self) -> str:
        return ""

    def output(self):
        return luigi.LocalTarget(
            str(root_dir() / "exceptions" / self.format / self.tool() / self.driver_name / f"exceptions-of-{self.runs}-runs.csv"))

    def run(self):
        all_exceptions = defaultdict(Counter)
        for f in self.input():
            run_exceptions = set()
            try:
                with open(str(Path(f.path).with_suffix(".exceptions.json"))) as path:
                    exc_run = json.load(path)
                    for exc in exc_run:
                        run_exceptions.add((exc["name"], exc["location"]))
            except FileNotFoundError:
                pass  # no exceptions were reported for this run
            for n, loc in run_exceptions:
                all_exceptions[n][loc] += 1
        with self.output().open("w") as out:
            w = csv.writer(out)
            w.writerow(["exception", "location", f"{self.tool()} detection rate"])
            for exc, run_exceptions in all_exceptions.items():
                for loc, count in run_exceptions.items():
                    w.writerow((exc, loc, count / self.runs))


class MergeKPathExceptions(KPath, MergeExceptions):
    def requires(self):
        return [ExecuteKRun(format=self.format, run_num=run, driver_name=self.driver_name, k=self.k) for run in range(self.runs)]


class MergeGrammarinatorExceptions(Grammarinator, MergeExceptions):
    def requires(self):
        return [ExecuteGrammarinatorRun(format=self.format, run_num=run, driver_name=self.driver_name, k=self.k) for run in range(self.runs)]


class CombineExceptionResults(luigi.Task):
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    runs: int = luigi.IntParameter(description="Number of runs to generate", positional=False)
    k_params: List[int] = luigi.ListParameter(description="The k values for the k-path algorithm", positional=False)
    task_namespace = "postprocessing"

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "postprocessing" / "exceptions" / self.format / f"exceptions-over-{self.runs}-runs.csv"))

    def requires(self):
        drivers = ExperimentConfig().drivers[self.format]["drivers"]
        return {
            **{f"{k}-path": {name: MergeKPathExceptions(format=self.format, runs=self.runs, driver_name=name, k=k) for name in drivers.keys()} for k in self.k_params},
            **{f"{k}-grammarinator": {name: MergeGrammarinatorExceptions(format=self.format, runs=self.runs, driver_name=name, k=k) for name in drivers.keys()} for k in self.k_params},
        }

    def run(self):
        df = pd.DataFrame()
        for driver in ExperimentConfig().drivers[self.format]["drivers"].keys():
            dfs = [pd.read_csv(self.input()[f"{k}-path"][driver].path) for k in self.k_params]
            dfs.extend([pd.read_csv(self.input()[f"{k}-grammarinator"][driver].path) for k in self.k_params])
            sf = reduce(lambda left, right: pd.merge(left, right, on=["exception", "location"], how="outer"), dfs).fillna(0)

            sf.insert(0, "subject", driver)
            sf.set_index(["subject", "exception", "location"], inplace=True)

            df = pd.concat((df, sf))
        with self.output().temporary_path() as res:
            df.to_csv(res, encoding='utf-8')


class ConcatenateRuns(luigi.Task):
    __metaclass__ = abc.ABCMeta
    runs: int = luigi.IntParameter(description="Number of runs to generate", positional=False)
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    driver_name: str = luigi.Parameter(description="The driver name", positional=False)
    task_namespace = "postprocessing"

    @abc.abstractmethod
    def tool(self) -> str:
        return ""

    @abc.abstractmethod
    def requirement(self, number) -> luigi.Task:
        return

    def requires(self):
        return [self.requirement(run) for run in range(self.runs)]

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "postprocessing" / "concatenated" / self.format / self.driver_name / f"concat-of-{self.runs}-{self.tool()}-runs.csv"))

    def run(self):
        dfs = []
        for run in range(self.runs):
            df = pd.read_csv(self.input()[run].path)
            df["run"] = run
            dfs.append(df)
        dd = pd.concat(dfs)
        dd["tool"] = self.tool()
        dd["format"] = self.format
        dd["subject"] = self.driver_name
        with self.output().temporary_path() as res:
            dd.to_csv(res, encoding='utf-8', index=False)


class ConcatenateKRuns(KPath, ConcatenateRuns):
    def requirement(self, number):
        return ExecuteKRun(format=self.format, run_num=number, driver_name=self.driver_name, k=self.k)


class ConcatenateGrammarinatorStats(Grammarinator, ConcatenateRuns):
    def requirement(self, number) -> luigi.Task:
        return ExecuteGrammarinatorRun(format=self.format, run_num=number, driver_name=self.driver_name, k=self.k)


class ConcatDrivers(luigi.Task):
    runs: int = luigi.IntParameter(description="Number of runs to generate", positional=False)
    format: str = luigi.Parameter(description="The name of the format directory (e.g. json)", positional=False)
    k_params: List[int] = luigi.ListParameter(description="The k values for the k-path algorithm", positional=False)
    task_namespace = "orchestration"

    def requires(self):
        format_info = ExperimentConfig().drivers[self.format]
        drivers = format_info["drivers"].keys()
        return {
            **{f"{k}-path-detailed": {name: ConcatenateKRuns(format=self.format, k=k, runs=self.runs, driver_name=name) for name in drivers} for k in self.k_params},
            **{f"grammarinator-{k}-detailed": {name: ConcatenateGrammarinatorStats(format=self.format, k=k, runs=self.runs, driver_name=name) for name in drivers} for k in self.k_params},
        }

    def output(self):
        return luigi.LocalTarget(str(root_dir() / "postprocessing" / "concatenated" / self.format / f"concat-of-{self.runs}-{self.format}-runs.csv"))

    def run(self):
        dd = pd.concat((pd.read_csv(inp.output().path) for inp in self.deps()))
        with self.output().temporary_path() as res:
            dd.to_csv(res, encoding='utf-8', index=False)


class RunAllDrivers(luigi.WrapperTask):
    runs: int = luigi.IntParameter(description="Number of random runs to generate", positional=False)
    k_params: List[int] = luigi.ListParameter(description="The k values for the k-path algorithm", positional=False)
    task_namespace = "orchestration"

    def requires(self):
        yield [ConcatDrivers(runs=self.runs, k_params=self.k_params, format=fmt) for fmt in ExperimentConfig().drivers.keys()]
        yield [CombineExceptionResults(runs=self.runs, k_params=self.k_params, format=fmt) for fmt in ExperimentConfig().drivers.keys()]


class GenerateAndRunNotebook(luigi.Task):
    runs: int = luigi.IntParameter(description="Number of random runs to generate", positional=False)
    suffix = luigi.Parameter(description="The suffix to append to the notebook name", positional=False)
    k_params: List[int] = luigi.ListParameter(description="The k values for the k-path algorithm", positional=False)
    task_namespace = "presentation"

    def requires(self):
        return RunAllDrivers(runs=self.runs, k_params=self.k_params)

    def output(self):
        suffix = f"-{self.suffix}" if self.suffix else ""
        return luigi.LocalTarget(str(root_dir() / f"report-{self.runs}-runs{suffix}.ipynb"))

    def run(self):
        nb = nbf.v4.new_notebook()
        cells = []
        cells.append(nbf.v4.new_markdown_cell("# Imports"))
        cells.append(nbf.v4.new_code_cell("""\
import pandas as pd
pd.set_option("display.max_colwidth", -1)

import plotly.offline as py
import plotly.graph_objs as go
py.init_notebook_mode(connected=True)
import cufflinks
cufflinks.go_offline()

import matplotlib.pyplot as plt
%matplotlib inline

import seaborn as sns
sns.set(font_scale=1.75)
color_map = sns.light_palette("green", as_cmap=True)"""))
        cells.append(nbf.v4.new_markdown_cell(f"""\
# Configuration
- experiment repetitions: `{self.runs}`
"""))
        cells.append(nbf.v4.new_markdown_cell("# Utility Functions"))
        cells.append(nbf.v4.new_code_cell("""\
def load_fmt_data(fmt: str, runs: int):
    return pd.read_csv(f"postprocessing/concatenated/{fmt}/concat-of-{runs}-{fmt}-runs.csv", index_col=False)"""))
        cells.append(nbf.v4.new_code_cell("""\
def show_exceptions(fmt: str, runs: int):
    df = pd.read_csv(f"postprocessing/exceptions/{fmt}/exceptions-over-{runs}-runs.csv", index_col=["subject", "exception", "location"]).sort_index()
    return df.style.background_gradient(cmap=color_map, low=0.0, high=1.0).format("{:,.0%}")"""))
        cells.append(nbf.v4.new_code_cell("""\
def mean_coverage_comparison(fmt: str, runs: int):
    d = load_fmt_data(fmt, runs)
    # select for each run the row with the greatest file number
    grp = d.groupby(["format", "subject", "tool", "run"]).agg({"filenum": "max"})
    grp.reset_index(inplace=True)
    d = pd.merge(d, grp)
    d = d[["branch", "tool", "subject"]]
    d = d.groupby(["subject", "tool"])
    d = d.mean()
    d = d.reset_index()
    d = d.pivot(index="subject", columns="tool", values="branch")
    d.iplot(kind="bar", title=f"Format {fmt}", xTitle="Subject", yTitle="Mean Branch Coverage")"""))
        cells.append(nbf.v4.new_code_cell("""\
def coverage_progress_comparison(fmt: str, subject: str, runs: int):
    df = load_fmt_data(fmt, runs)
    df = df[df.subject==subject]
    plt.figure(figsize=(20, 8))
    ax = sns.lineplot(data=df, x="filenum", y="branch", hue="tool", style="tool", dashes=False)#, units="run", estimator=None)
    ax.set_title(f"Subject {subject}")
    ax.set_xlabel("Number of Files")
    ax.set_ylabel("Branch Coverage")"""))
        cells.append(nbf.v4.new_code_cell("""\
def plot_coverage_dispersion(fmt: str, runs: int):
    df = load_fmt_data(fmt, runs)
    # select for each run the row with the greatest file number
    grp = df.groupby(["format", "subject", "tool", "run"]).agg({"filenum": "max"})
    grp.reset_index(inplace=True)
    df = pd.merge(df, grp)
    df = df[["subject", "tool", "branch", "run"]]
    for sub in df.subject.unique():
        df[df.subject==sub][["tool", "branch", "run"]]\\
        .pivot(index="run", columns="tool", values="branch")\\
        .iplot(kind="box", title=f"Subject {sub}", xTitle="Tool", yTitle="Branch Coverage")"""))
        cells.append(nbf.v4.new_markdown_cell("# Evaluation"))

        for fmt, subject in ExperimentConfig().drivers.items():
            cells.append(nbf.v4.new_markdown_cell(f"## Format {fmt} ({len(subject['drivers'])} subjects)"))
            cells.append(nbf.v4.new_code_cell(f"show_exceptions({repr(fmt)}, runs={self.runs})"))
            cells.append(nbf.v4.new_code_cell(f"mean_coverage_comparison({repr(fmt)}, runs={self.runs})"))
            cells.append(nbf.v4.new_code_cell("\n".join([f"coverage_progress_comparison({repr(fmt)}, {repr(driver)}, runs={self.runs})" for driver in subject["drivers"]])))
            cells.append(nbf.v4.new_code_cell(f"plot_coverage_dispersion({repr(fmt)}, runs={self.runs})"))
        nb['cells'] = cells

        ep = ExecutePreprocessor(kernel_name="python3", timeout=None, allow_errors=True, interrupt_on_timeout=True)
        ep.preprocess(nb, {'metadata': {'path': str(root_dir())}})

        with self.output().open('w') as f:
            nbf.write(nb, f)


class RenderNotebook(luigi.Task):
    runs = luigi.IntParameter(description="Number of random runs to generate", positional=False)
    k_params: List[int] = luigi.ListParameter(description="The k values for the k-path algorithm", positional=False)
    suffix = luigi.Parameter(description="The suffix to append to the notebook name", default="", positional=False)
    task_namespace = 'presentation'

    def requires(self):
        return GenerateAndRunNotebook(k_params=self.k_params, runs=self.runs, suffix=self.suffix)

    def output(self):
        return luigi.LocalTarget(str(Path(self.input().path).with_suffix(".html")))

    def run(self):
        html_exporter = HTMLExporter()
        html, _ = html_exporter.from_filename(self.input().path)
        with self.output().open('w') as f:
            f.write(html)


if __name__ == '__main__':
    logging.basicConfig(format="%(asctime)s %(levelname)s %(message)s", datefmt="%d.%m.%Y %H:%M:%S", level=logging.INFO, stream=sys.stdout)
    luigi.run(main_task_cls=RenderNotebook, local_scheduler=False)
