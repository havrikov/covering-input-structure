import saarland.cispa.se.tribble.dsl._

// translated from https://github.com/antlr/grammars-v4/blob/master/csv/CSV.g4
// as of commit 323e4a99aa693d057a413dde99469e4c70aa8916

Grammar(
  'csvFile := 'hdr ~ 'row.rep(1),

  'hdr := 'row,

  'row := 'field ~ ("," ~ 'field).rep ~ "\r".? ~ "\n",

  'field := 'TEXT | 'STRING | "",

  'TEXT   := "[^,\n\r\"]+".regex,

  'STRING := "\"" ~ ("\"\"" | "~[\"]".regex).rep ~ "\"" // quote-quote is an escaped quote
)
