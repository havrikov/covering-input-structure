import saarland.cispa.se.tribble.dsl._

// translated from https://github.com/antlr/grammars-v4/blob/master/json/JSON.g4
// as of commit 16612bc74aea6e07afb9e0f54d9c7c5fac1475b1

Grammar(
  'json := 'value,

  'obj := "{" ~ 'OWS ~ 'pair ~ 'OWS ~ ("," ~ 'OWS ~ 'pair).rep ~ 'OWS ~ "}" | "{" ~ 'OWS ~ "}",

  'pair := 'STRING ~ 'OWS ~ ":" ~ 'OWS ~ 'value,

  'array := "[" ~ 'OWS ~ 'value ~ 'OWS ~ ("," ~ 'OWS ~ 'value).rep ~ 'OWS ~ "]" | "[" ~ 'OWS ~ "]",

  'value := 'STRING
    | 'NUMBER
    | 'obj
    | 'array
    | "true"
    | "false"
    | "null"
  ,

  'STRING := "\"" ~ ('ESC | 'SAFECODEPOINT).rep ~ "\"",

  'ESC := "\\" ~ ("""[\"\\/bfnrt]""".regex | 'UNICODE),

  'UNICODE := "u" ~ 'HEX ~ 'HEX ~ 'HEX ~ 'HEX,

  'HEX := "[0-9a-fA-F]".regex,

  'SAFECODEPOINT := "[^\u0000-\u001F\\\"\\\\]".regex,

  'NUMBER := "-".? ~ 'INT ~ ("." ~ "[0-9]+".regex).? ~ 'EXP.?,

  'INT := "0" | "[1-9][0-9]*".regex,

  'EXP := "[Ee][+-]?".regex ~ 'INT,

  'OWS := 'WS.rep,

  'WS := " " | "\t" | "\n" | "\r"
)
