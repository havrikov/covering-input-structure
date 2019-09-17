import saarland.cispa.se.tribble.dsl._

// translated from https://github.com/antlr/grammars-v4/blob/master/url/url.g4
// as of commit 1cc88dd0773e0528c60da5ccb90ed611e6fe6394

Grammar(
  'url := 'uri,

  'uri := 'scheme ~ "://" ~ 'login.? ~ 'host ~ (":" ~ 'port).? ~ ("/" ~ 'path).? ~ 'query.? ~ 'frag.?,

  'scheme := 'string,

  'host := "/".? ~ ('hostname | 'hostnumber),

  'hostname := 'string ~ ("." ~ 'string).rep,

  'hostnumber := 'DIGITS ~ "." ~ 'DIGITS ~ "." ~ 'DIGITS ~ "." ~ 'DIGITS,

  'port := 'DIGITS,

  'path := 'string ~ ("/" ~ 'string).rep,

  'user := 'string,

  'login := 'user ~ ":" ~ 'password ~ "@",

  'password := 'string,

  'frag := "#" ~ 'string,

  'query := "?" ~ 'search,

  'search := 'searchparameter ~ ("&" ~ 'searchparameter).rep,

  'searchparameter := 'string ~ ("=" ~ ('string | 'DIGITS | 'HEX)).?,

  'string := 'STRING,

  'DIGITS := "[0-9]+".regex,

  'HEX := "(%[a-fA-F0-9][a-fA-F0-9])+".regex,

  'STRING := ("[a-zA-Z~0-9]".regex | 'HEX) ~ ("[a-zA-Z0-9.-]".regex | 'HEX).rep,
)
