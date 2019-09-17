# K-Path Coverage Evaluation

This is a replication package for the paper "Systematically Covering Input Structure" published at ASE 2019.

You can download the entire dataset as reported in the paper from [Google Drive](https://drive.google.com/open?id=1S_F5EWB0B5v8cxkTsXArvG0ViPs7wryS) (574MB downloaded, 16GB uncompressed).

You can also run the experiments yourself: 

## Prerequisites
You need Python `>= 3.6` and Java `>= 1.8`.

## Building

Clone this repo with _submodules_:

```bash
git clone --recurse-submodules git@github.com:havrikov/covering-input-structure.git
```

Install python dependencies:

```bash
pip install -r requirements.txt
```

## Configuring

Edit `luigi.cfg` to set the amount of RAM available and the experiment directory.

## Running

Start the luigi daemon (installed as part of the prerequisites).

```bash
luigid --background
```

Run the experiments. (Substitute the number of CPUs below)

```bash
python ./experiments.py --k-params "[1,2,3,5]" --runs 50 --workers <number-of-CPUs>
```

Navigate to http://localhost:8082 to monitor the progress.

> **NOTE** The process will take _a long time_ for a high number of runs, so preferably launch it on some sort of compute server.

## Inspecting Results

After the experiment pipeline has finished, the results will be available as `report.html` in your experiment directory.
