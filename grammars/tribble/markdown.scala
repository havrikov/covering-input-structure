import saarland.cispa.se.tribble.dsl._

//  Translated  from  https://github.com/jgm/peg-markdown/blob/master/markdown_parser.leg

Grammar(
  'Doc := 'BOM.? ~ 'StartList ~ 'Block.rep,

  'Block := 'BlankLine.rep ~
    ('BlockQuote
      | 'Verbatim
      | 'Note
      | 'Reference
      | 'HorizontalRule
      | 'Heading
      | 'OrderedList
      | 'BulletList
      | 'HtmlBlock
      | 'StyleBlock
      | 'Para
      | 'Plain)

  ,

  'Para := 'NonindentSpace ~ 'Inlines ~ 'BlankLine.rep(1)


  ,
  'Plain := 'Inlines

  ,
  'AtxInline := 'Inline

  ,
  'AtxStart := "######" | "#####" | "####" | "###" | "##" | "#"


  ,
  'AtxHeading := 'AtxStart ~ 'Sp ~ 'StartList ~ 'AtxInline.rep(1) ~ ('Sp ~ "#".rep ~ 'Sp).? ~ 'Newline


  ,
  'SetextHeading := 'SetextHeading1 | 'SetextHeading2

  ,
  'SetextBottom1 := "=".rep(1) ~ 'Newline

  ,
  'SetextBottom2 := "-".rep(1) ~ 'Newline

  ,
  'SetextHeading1 := 'StartList ~ 'Inline.rep(1) ~ 'Sp ~ 'Newline ~ 'SetextBottom1

  ,
  'SetextHeading2 := 'StartList ~ 'Inline.rep(1) ~ 'Sp ~ 'Newline ~ 'SetextBottom2

  ,
  'Heading := 'SetextHeading | 'AtxHeading

  ,
  'BlockQuote := 'BlockQuoteRaw


  ,
  'BlockQuoteRaw := 'StartList ~ (">" ~ " ".? ~ 'Line ~ 'Line.rep ~ 'BlankLine.rep).rep(1)


  ,
  'NonblankIndentedLine := 'IndentedLine

  ,
  'VerbatimChunk := 'StartList ~ 'BlankLine.rep ~ 'NonblankIndentedLine.rep(1)


  ,
  'Verbatim := 'StartList ~ 'VerbatimChunk.rep(1)


  ,
  'HorizontalRule := 'NonindentSpace ~
    ("*" ~ 'Sp ~ "*" ~ 'Sp ~ "*" ~ ('Sp ~ "*").rep
      | "-" ~ 'Sp ~ "-" ~ 'Sp ~ "-" ~ ('Sp ~ "-").rep
      | "_" ~ 'Sp ~ "_" ~ 'Sp ~ "_" ~ ('Sp ~ "_").rep) ~
    'Sp ~ 'Newline ~ 'BlankLine.rep(1)


  ,
  'Bullet := 'NonindentSpace ~ ("+" | "*" | "-") ~ 'Spacechar.rep(1)

  ,
  'BulletList := ('ListTight | 'ListLoose)


  ,
  'ListTight := 'StartList ~ 'ListItemTight.rep(1) ~ 'BlankLine.rep

  ,
  'ListLoose := 'StartList ~ ('ListItem ~ 'BlankLine.rep).rep(1)


  ,
  'ListItem := ('Bullet | 'Enumerator) ~ 'StartList ~ 'ListBlock ~ 'ListContinuationBlock.rep,


  'ListItemTight := ('Bullet | 'Enumerator) ~ 'StartList ~ 'ListBlock ~ 'ListContinuationBlock.rep


  ,
  'ListBlock := 'StartList ~ 'Line ~ 'ListBlockLine.rep


  ,
  'ListContinuationBlock := 'StartList ~ 'BlankLine.rep ~ ('Indent ~ 'ListBlock).rep(1)


  ,
  'Enumerator := 'NonindentSpace ~ "[0-9]".regex.rep(1) ~ "." ~ 'Spacechar.rep(1)

  ,
  'OrderedList := 'ListTight | 'ListLoose


  ,
  'ListBlockLine :=


    'OptionallyIndentedLine

  //   'Parsers  'for  'different  'kinds  'of  'block-level  'HTML  'content.
  //   'This  'is  'repetitive  'due  'to  'constraints  'of  'PEG  'grammar.

  ,
  'HtmlBlockOpenAddress := "<" ~ 'Spnl ~ ("address" | "ADDRESS") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseAddress := "<" ~ 'Spnl ~ "/" ~ ("address" | "ADDRESS") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockAddress := 'HtmlBlockOpenAddress ~ 'HtmlBlockAddress.rep ~ 'HtmlBlockCloseAddress

  ,
  'HtmlBlockOpenBlockquote := "<" ~ 'Spnl ~ ("blockquote" | "BLOCKQUOTE") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseBlockquote := "<" ~ 'Spnl ~ "/" ~ ("blockquote" | "BLOCKQUOTE") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockBlockquote := 'HtmlBlockOpenBlockquote ~ 'HtmlBlockBlockquote.rep ~ 'HtmlBlockCloseBlockquote

  ,
  'HtmlBlockOpenCenter := "<" ~ 'Spnl ~ ("center" | "CENTER") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseCenter := "<" ~ 'Spnl ~ "/" ~ ("center" | "CENTER") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockCenter := 'HtmlBlockOpenCenter ~ 'HtmlBlockCenter.rep ~ 'HtmlBlockCloseCenter

  ,
  'HtmlBlockOpenDir := "<" ~ 'Spnl ~ ("dir" | "DIR") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseDir := "<" ~ 'Spnl ~ "/" ~ ("dir" | "DIR") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockDir := 'HtmlBlockOpenDir ~ 'HtmlBlockDir.rep ~ 'HtmlBlockCloseDir

  ,
  'HtmlBlockOpenDiv := "<" ~ 'Spnl ~ ("div" | "DIV") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseDiv := "<" ~ 'Spnl ~ "/" ~ ("div" | "DIV") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockDiv := 'HtmlBlockOpenDiv ~ 'HtmlBlockDiv.rep ~ 'HtmlBlockCloseDiv

  ,
  'HtmlBlockOpenDl := "<" ~ 'Spnl ~ ("dl" | "DL") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseDl := "<" ~ 'Spnl ~ "/" ~ ("dl" | "DL") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockDl := 'HtmlBlockOpenDl ~ 'HtmlBlockDl.rep ~ 'HtmlBlockCloseDl

  ,
  'HtmlBlockOpenFieldset := "<" ~ 'Spnl ~ ("fieldset" | "FIELDSET") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseFieldset := "<" ~ 'Spnl ~ "/" ~ ("fieldset" | "FIELDSET") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockFieldset := 'HtmlBlockOpenFieldset ~ 'HtmlBlockFieldset.rep ~ 'HtmlBlockCloseFieldset

  ,
  'HtmlBlockOpenForm := "<" ~ 'Spnl ~ ("form" | "FORM") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseForm := "<" ~ 'Spnl ~ "/" ~ ("form" | "FORM") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockForm := 'HtmlBlockOpenForm ~ 'HtmlBlockForm.rep ~ 'HtmlBlockCloseForm

  ,
  'HtmlBlockOpenH1 := "<" ~ 'Spnl ~ ("h1" | "H1") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH1 := "<" ~ 'Spnl ~ "/" ~ ("h1" | "H1") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH1 := 'HtmlBlockOpenH1 ~ 'HtmlBlockH1.rep ~ 'HtmlBlockCloseH1

  ,
  'HtmlBlockOpenH2 := "<" ~ 'Spnl ~ ("h2" | "H2") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH2 := "<" ~ 'Spnl ~ "/" ~ ("h2" | "H2") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH2 := 'HtmlBlockOpenH2 ~ 'HtmlBlockH2.rep ~ 'HtmlBlockCloseH2

  ,
  'HtmlBlockOpenH3 := "<" ~ 'Spnl ~ ("h3" | "H3") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH3 := "<" ~ 'Spnl ~ "/" ~ ("h3" | "H3") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH3 := 'HtmlBlockOpenH3 ~ 'HtmlBlockH3.rep ~ 'HtmlBlockCloseH3

  ,
  'HtmlBlockOpenH4 := "<" ~ 'Spnl ~ ("h4" | "H4") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH4 := "<" ~ 'Spnl ~ "/" ~ ("h4" | "H4") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH4 := 'HtmlBlockOpenH4 ~ 'HtmlBlockH4.rep ~ 'HtmlBlockCloseH4

  ,
  'HtmlBlockOpenH5 := "<" ~ 'Spnl ~ ("h5" | "H5") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH5 := "<" ~ 'Spnl ~ "/" ~ ("h5" | "H5") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH5 := 'HtmlBlockOpenH5 ~ 'HtmlBlockH5.rep ~ 'HtmlBlockCloseH5

  ,
  'HtmlBlockOpenH6 := "<" ~ 'Spnl ~ ("h6" | "H6") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseH6 := "<" ~ 'Spnl ~ "/" ~ ("h6" | "H6") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockH6 := 'HtmlBlockOpenH6 ~ 'HtmlBlockH6.rep ~ 'HtmlBlockCloseH6

  ,
  'HtmlBlockOpenMenu := "<" ~ 'Spnl ~ ("menu" | "MENU") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseMenu := "<" ~ 'Spnl ~ "/" ~ ("menu" | "MENU") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockMenu := 'HtmlBlockOpenMenu ~ 'HtmlBlockMenu.rep ~ 'HtmlBlockCloseMenu

  ,
  'HtmlBlockOpenNoframes := "<" ~ 'Spnl ~ ("noframes" | "NOFRAMES") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseNoframes := "<" ~ 'Spnl ~ "/" ~ ("noframes" | "NOFRAMES") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockNoframes := 'HtmlBlockOpenNoframes ~ 'HtmlBlockNoframes.rep ~ 'HtmlBlockCloseNoframes

  ,
  'HtmlBlockOpenNoscript := "<" ~ 'Spnl ~ ("noscript" | "NOSCRIPT") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseNoscript := "<" ~ 'Spnl ~ "/" ~ ("noscript" | "NOSCRIPT") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockNoscript := 'HtmlBlockOpenNoscript ~ 'HtmlBlockNoscript.rep ~ 'HtmlBlockCloseNoscript

  ,
  'HtmlBlockOpenOl := "<" ~ 'Spnl ~ ("ol" | "OL") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseOl := "<" ~ 'Spnl ~ "/" ~ ("ol" | "OL") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockOl := 'HtmlBlockOpenOl ~ 'HtmlBlockOl.rep ~ 'HtmlBlockCloseOl

  ,
  'HtmlBlockOpenP := "<" ~ 'Spnl ~ ("p" | "P") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseP := "<" ~ 'Spnl ~ "/" ~ ("p" | "P") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockP := 'HtmlBlockOpenP ~ 'HtmlBlockP.rep ~ 'HtmlBlockCloseP

  ,
  'HtmlBlockOpenPre := "<" ~ 'Spnl ~ ("pre" | "PRE") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockClosePre := "<" ~ 'Spnl ~ "/" ~ ("pre" | "PRE") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockPre := 'HtmlBlockOpenPre ~ 'HtmlBlockPre.rep ~ 'HtmlBlockClosePre

  ,
  'HtmlBlockOpenTable := "<" ~ 'Spnl ~ ("table" | "TABLE") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTable := "<" ~ 'Spnl ~ "/" ~ ("table" | "TABLE") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTable := 'HtmlBlockOpenTable ~ 'HtmlBlockTable.rep ~ 'HtmlBlockCloseTable

  ,
  'HtmlBlockOpenUl := "<" ~ 'Spnl ~ ("ul" | "UL") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseUl := "<" ~ 'Spnl ~ "/" ~ ("ul" | "UL") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockUl := 'HtmlBlockOpenUl ~ 'HtmlBlockUl.rep ~ 'HtmlBlockCloseUl

  ,
  'HtmlBlockOpenDd := "<" ~ 'Spnl ~ ("dd" | "DD") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseDd := "<" ~ 'Spnl ~ "/" ~ ("dd" | "DD") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockDd := 'HtmlBlockOpenDd ~ 'HtmlBlockDd.rep ~ 'HtmlBlockCloseDd

  ,
  'HtmlBlockOpenDt := "<" ~ 'Spnl ~ ("dt" | "DT") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseDt := "<" ~ 'Spnl ~ "/" ~ ("dt" | "DT") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockDt := 'HtmlBlockOpenDt ~ 'HtmlBlockDt.rep ~ 'HtmlBlockCloseDt

  ,
  'HtmlBlockOpenFrameset := "<" ~ 'Spnl ~ ("frameset" | "FRAMESET") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseFrameset := "<" ~ 'Spnl ~ "/" ~ ("frameset" | "FRAMESET") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockFrameset := 'HtmlBlockOpenFrameset ~ 'HtmlBlockFrameset.rep ~ 'HtmlBlockCloseFrameset

  ,
  'HtmlBlockOpenLi := "<" ~ 'Spnl ~ ("li" | "LI") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseLi := "<" ~ 'Spnl ~ "/" ~ ("li" | "LI") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockLi := 'HtmlBlockOpenLi ~ 'HtmlBlockLi.rep ~ 'HtmlBlockCloseLi

  ,
  'HtmlBlockOpenTbody := "<" ~ 'Spnl ~ ("tbody" | "TBODY") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTbody := "<" ~ 'Spnl ~ "/" ~ ("tbody" | "TBODY") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTbody := 'HtmlBlockOpenTbody ~ 'HtmlBlockTbody.rep ~ 'HtmlBlockCloseTbody

  ,
  'HtmlBlockOpenTd := "<" ~ 'Spnl ~ ("td" | "TD") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTd := "<" ~ 'Spnl ~ "/" ~ ("td" | "TD") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTd := 'HtmlBlockOpenTd ~ 'HtmlBlockTd.rep ~ 'HtmlBlockCloseTd

  ,
  'HtmlBlockOpenTfoot := "<" ~ 'Spnl ~ ("tfoot" | "TFOOT") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTfoot := "<" ~ 'Spnl ~ "/" ~ ("tfoot" | "TFOOT") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTfoot := 'HtmlBlockOpenTfoot ~ 'HtmlBlockTfoot.rep ~ 'HtmlBlockCloseTfoot

  ,
  'HtmlBlockOpenTh := "<" ~ 'Spnl ~ ("th" | "TH") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTh := "<" ~ 'Spnl ~ "/" ~ ("th" | "TH") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTh := 'HtmlBlockOpenTh ~ 'HtmlBlockTh.rep ~ 'HtmlBlockCloseTh

  ,
  'HtmlBlockOpenThead := "<" ~ 'Spnl ~ ("thead" | "THEAD") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseThead := "<" ~ 'Spnl ~ "/" ~ ("thead" | "THEAD") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockThead := 'HtmlBlockOpenThead ~ 'HtmlBlockThead.rep ~ 'HtmlBlockCloseThead

  ,
  'HtmlBlockOpenTr := "<" ~ 'Spnl ~ ("tr" | "TR") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseTr := "<" ~ 'Spnl ~ "/" ~ ("tr" | "TR") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockTr := 'HtmlBlockOpenTr ~ 'HtmlBlockTr.rep ~ 'HtmlBlockCloseTr

  ,
  'HtmlBlockOpenScript := "<" ~ 'Spnl ~ ("script" | "SCRIPT") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseScript := "<" ~ 'Spnl ~ "/" ~ ("script" | "SCRIPT") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockScript := 'HtmlBlockOpenScript ~ 'HtmlBlockCloseScript

  ,
  'HtmlBlockOpenHead := "<" ~ 'Spnl ~ ("head" | "HEAD") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'HtmlBlockCloseHead := "<" ~ 'Spnl ~ "/" ~ ("head" | "HEAD") ~ 'Spnl ~ ">"
  ,
  'HtmlBlockHead := 'HtmlBlockOpenHead ~ 'HtmlBlockCloseHead

  ,
  'HtmlBlockInTags := 'HtmlBlockAddress
    | 'HtmlBlockBlockquote
    | 'HtmlBlockCenter
    | 'HtmlBlockDir
    | 'HtmlBlockDiv
    | 'HtmlBlockDl
    | 'HtmlBlockFieldset
    | 'HtmlBlockForm
    | 'HtmlBlockH1
    | 'HtmlBlockH2
    | 'HtmlBlockH3
    | 'HtmlBlockH4
    | 'HtmlBlockH5
    | 'HtmlBlockH6
    | 'HtmlBlockMenu
    | 'HtmlBlockNoframes
    | 'HtmlBlockNoscript
    | 'HtmlBlockOl
    | 'HtmlBlockP
    | 'HtmlBlockPre
    | 'HtmlBlockTable
    | 'HtmlBlockUl
    | 'HtmlBlockDd
    | 'HtmlBlockDt
    | 'HtmlBlockFrameset
    | 'HtmlBlockLi
    | 'HtmlBlockTbody
    | 'HtmlBlockTd
    | 'HtmlBlockTfoot
    | 'HtmlBlockTh
    | 'HtmlBlockThead
    | 'HtmlBlockTr
    | 'HtmlBlockScript
    | 'HtmlBlockHead

  ,
  'HtmlBlock := ('HtmlBlockInTags | 'HtmlComment | 'HtmlBlockSelfClosing) ~ 'BlankLine.rep(1)


  ,
  'HtmlBlockSelfClosing := "<" ~ 'Spnl ~ 'HtmlBlockType ~ 'Spnl ~ 'HtmlAttribute.rep ~ "/" ~ 'Spnl ~ ">"

  ,
  'HtmlBlockType := "address" | "blockquote" | "center" | "dir" | "div" | "dl" | "fieldset" | "form" | "h1" | "h2" | "h3" |
    "h4" | "h5" | "h6" | "hr" | "isindex" | "menu" | "noframes" | "noscript" | "ol" | "p" | "pre" | "table" |
    "ul" | "dd" | "dt" | "frameset" | "li" | "tbody" | "td" | "tfoot" | "th" | "thead" | "tr" | "script" |
    "ADDRESS" | "BLOCKQUOTE" | "CENTER" | "DIR" | "DIV" | "DL" | "FIELDSET" | "FORM" | "H1" | "H2" | "H3" |
    "H4" | "H5" | "H6" | "HR" | "ISINDEX" | "MENU" | "NOFRAMES" | "NOSCRIPT" | "OL" | "P" | "PRE" | "TABLE" |
    "UL" | "DD" | "DT" | "FRAMESET" | "LI" | "TBODY" | "TD" | "TFOOT" | "TH" | "THEAD" | "TR" | "SCRIPT"

  ,
  'StyleOpen := "<" ~ 'Spnl ~ ("style" | "STYLE") ~ 'Spnl ~ 'HtmlAttribute.rep ~ ">"
  ,
  'StyleClose := "<" ~ 'Spnl ~ "/" ~ ("style" | "STYLE") ~ 'Spnl ~ ">"
  ,
  'InStyleTags := 'StyleOpen ~ 'StyleClose
  ,
  'StyleBlock := 'InStyleTags ~ 'BlankLine.rep


  ,
  'Inlines := 'StartList ~ ('Inline
    | 'Endline ~ 'Inline).rep(1) ~ 'Endline.?


  ,
  'Inline := 'Str
    | 'Endline
    | 'UlOrStarLine
    | 'Space
    | 'Strong
    | 'Emph
    | 'Strike
    | 'Image
    | 'Link
    | 'NoteReference
    | 'InlineNote
    | 'Code
    | 'RawHtml
    | 'Entity
    | 'EscapedChar
    | 'Smart
    | 'Symbol

  ,
  'Space := 'Spacechar.rep(1)


  ,
  'Str := 'StartList ~ 'NormalChar.rep(1) ~ 'StrChunk.rep


  ,
  'StrChunk := ('NormalChar | "_".rep(1) ~ 'Alphanumeric).rep(1) | 'AposChunk

  ,
  'AposChunk := "'" ~ 'Alphanumeric


  ,
  'EscapedChar := "\\" ~ """[\-\\`|*_{}[\]()# +.!><]""".regex


  ,
  'Entity := ('HexEntity | 'DecEntity | 'CharEntity)


  ,
  'Endline := 'LineBreak | 'TerminalEndline | 'NormalEndline

  ,
  'NormalEndline := 'Sp ~ 'Newline


  ,
  'TerminalEndline := 'Sp ~ 'Newline ~ 'Eof


  ,
  'LineBreak := "  " ~ 'NormalEndline


  ,
  'Symbol := 'SpecialChar


  //   'This  'keeps  'the  'parser  'from  'getting  'bogged  'down  'on  'long  'strings  'of '*'  'or '_',
  //   'or  'strings  'of '*'  'or '_'  'with  'space  'on  'each  'side:
  ,
  'UlOrStarLine := ('UlLine | 'StarLine)
  ,
  'StarLine := "****" ~ "*".rep | 'Spacechar ~ "*".rep(1) ~ 'Spacechar
  ,
  'UlLine := "____" ~ "_".rep | 'Spacechar ~ "_".rep(1) ~ 'Spacechar

  ,
  'Emph := 'EmphStar | 'EmphUl

  ,
//  'Whitespace := 'Spacechar | 'Newline,

  'EmphStar := "*" ~ 'StartList ~ ('Inline | 'StrongStar).rep(1) ~ "*",

  'EmphUl := "_" ~ 'StartList ~ ('Inline | 'StrongUl).rep(1) ~ "_",


  'Strong := 'StrongStar | 'StrongUl

  ,
  'StrongStar := "**" ~ 'StartList ~ 'Inline.rep(1) ~ "**",


  'StrongUl := "__" ~ 'StartList ~ 'Inline.rep(1) ~ "__"

  ,
  'Strike := "~~" ~ 'StartList ~ 'Inline.rep(1) ~ "~~"


  ,
  'Image := "!" ~ ('ExplicitLink | 'ReferenceLink)

  ,
  'Link := 'ExplicitLink | 'ReferenceLink | 'AutoLink

  ,
  'ReferenceLink := 'ReferenceLinkDouble | 'ReferenceLinkSingle

  ,
  'ReferenceLinkDouble := 'Label ~ 'Spnl ~ 'Label

  ,
  'ReferenceLinkSingle := 'Label ~ ('Spnl ~ "[]").?


  ,
  'ExplicitLink := 'Label ~ "(" ~ 'Sp ~ 'Source ~ 'Spnl ~ 'Title ~ 'Sp ~ ")"


  ,
  'Source := ("<" ~ 'SourceContents ~ ">" | 'SourceContents)


  ,
  'SourceContents := ('Nonspacechar.rep(1) | "(" ~ 'SourceContents ~ ")").rep

  ,
  'Title := ('TitleSingle | 'TitleDouble | "")


  ,
  'TitleSingle := "'" ~ "[^\n\r\\)']*".regex ~ "'"

  ,
  'TitleDouble := "\"" ~ "[^\n\r\\)\\\"]*".regex ~ "\""

  ,
  'AutoLink := 'AutoLinkUrl | 'AutoLinkEmail

  ,
  'AutoLinkUrl := "<" ~ "[A-Za-z]".regex.rep(1) ~ "://" ~ "[^>\n\r]+".regex ~ ">"


  ,
  'AutoLinkEmail := "<" ~ "mailto:".? ~ "[-A-Za-z0-9.\\+_./!%~$]+".regex ~ "@" ~ "[^>\n\r]+".regex ~ ">"


  ,
  'Reference := 'NonindentSpace ~ 'Label ~ ":" ~ 'Spnl ~ 'RefSrc ~ 'RefTitle ~ 'BlankLine.rep(1)


  ,
  'Label := "[" ~ 'StartList ~ 'Inline.rep ~ "]"

  ,
  'RefSrc := 'Nonspacechar.rep(1)


  ,
  'RefTitle := 'RefTitleSingle | 'RefTitleDouble | 'RefTitleParens | 'EmptyTitle


  ,
  'EmptyTitle := ""

  ,
  'RefTitleSingle := 'Spnl ~ "'" ~ "(' [\n\r]|[\n\r])*".regex ~ "'"

  ,
  'RefTitleDouble := 'Spnl ~ "\"" ~ "(\\\" [\n\r]|[\n\r])*".regex ~ "\""

  ,
  'RefTitleParens := 'Spnl ~ "(" ~ "(\\) [\n\r]|[\n\r])*".regex ~ ")"

  ,
//  'References := 'StartList ~ ('Reference | 'SkipBlock).rep,


  'Ticks1 := "`"
  ,
  'Ticks2 := "``"
  ,
  'Ticks3 := "```"
  ,
  'Ticks4 := "````"
  ,
  'Ticks5 := "`````"

  ,
  'Code := 'Ticks1 ~ 'Sp ~ ('Nonspacechar.rep(1) | "`".rep(1) | 'Spacechar | 'Newline).rep(1) ~ 'Sp ~ 'Ticks1
    | 'Ticks2 ~ 'Sp ~ ('Nonspacechar.rep(1) | "`".rep(1) | 'Spacechar | 'Newline).rep(1) ~ 'Sp ~ 'Ticks2
    | 'Ticks3 ~ 'Sp ~ ('Nonspacechar.rep(1) | "`".rep(1) | 'Spacechar | 'Newline).rep(1) ~ 'Sp ~ 'Ticks3
    | 'Ticks4 ~ 'Sp ~ ('Nonspacechar.rep(1) | "`".rep(1) | 'Spacechar | 'Newline).rep(1) ~ 'Sp ~ 'Ticks4
    | 'Ticks5 ~ 'Sp ~ ('Nonspacechar.rep(1) | "`".rep(1) | 'Spacechar | 'Newline).rep(1) ~ 'Sp ~ 'Ticks5
  ,
  'RawHtml := 'HtmlComment | 'HtmlBlockScript | 'HtmlTag

  ,
  'BlankLine := 'Sp ~ 'Newline

  ,
  'Quoted := "\"" ~ "[^\\\"]*".regex ~ "\"" | "'" ~ "[^']*".regex ~ "'"
  ,
  'HtmlAttribute := ('AlphanumericAscii | "-").rep(1) ~ 'Spnl ~ ("=" ~ 'Spnl ~ ('Quoted | 'Nonspacechar.rep(1))).? ~ 'Spnl
  ,
  'HtmlComment := "<!--" ~ "~(-->)".regex ~ "-->"
  ,
  'HtmlTag := "<" ~ 'Spnl ~ "/".? ~ 'AlphanumericAscii.rep(1) ~ 'Spnl ~ 'HtmlAttribute.rep ~ "/".? ~ 'Spnl ~ ">"
  ,
  'Eof := "\n"
  ,
  'Spacechar := " " | "\t"
  ,
  'Nonspacechar := "[^ \t\r\n]".regex
  ,
  'Newline := "\n" | "\r" ~ "\n".?
  ,
  'Sp := 'Spacechar.rep
  ,
  'Spnl := 'Sp ~ ('Newline ~ 'Sp).?
  ,
  'SpecialChar := "~" | "*" | "_" | "`" | "&" | "[" | "]" | "(" | ")" | "<" | "!" | "#" | "\\" | "'" | "\"" | 'ExtendedSpecialChar
  ,
  'NormalChar := "[^ \r\t\n\\~\\*_`&\\[\\]\\(\\)<!#\\\\'\\\"]".regex
  ,
  'Alphanumeric := "[0-9A-Za-z]".regex | "\200" | "\201" | "\202" | "\203" | "\204" | "\205" | "\206" | "\207" | "\210" | "\211" | "\212" | "\213" | "\214"
    | "\215" | "\216" | "\217" | "\220" | "\221" | "\222" | "\223" | "\224" | "\225" | "\226" | "\227" | "\230" | "\231" | "\232" | "\233" | "\234" | "\235"
    | "\236" | "\237" | "\240" | "\241" | "\242" | "\243" | "\244" | "\245" | "\246" | "\247" | "\250" | "\251" | "\252" | "\253" | "\254" | "\255" | "\256"
    | "\257" | "\260" | "\261" | "\262" | "\263" | "\264" | "\265" | "\266" | "\267" | "\270" | "\271" | "\272" | "\273" | "\274" | "\275" | "\276" | "\277"
    | "\300" | "\301" | "\302" | "\303" | "\304" | "\305" | "\306" | "\307" | "\310" | "\311" | "\312" | "\313" | "\314" | "\315" | "\316" | "\317" | "\320"
    | "\321" | "\322" | "\323" | "\324" | "\325" | "\326" | "\327" | "\330" | "\331" | "\332" | "\333" | "\334" | "\335" | "\336" | "\337" | "\340" | "\341"
    | "\342" | "\343" | "\344" | "\345" | "\346" | "\347" | "\350" | "\351" | "\352" | "\353" | "\354" | "\355" | "\356" | "\357" | "\360" | "\361" | "\362"
    | "\363" | "\364" | "\365" | "\366" | "\367" | "\370" | "\371" | "\372" | "\373" | "\374" | "\375" | "\376" | "\377"
  ,
  'AlphanumericAscii := "[A-Za-z0-9]".regex
  ,
//  'Digit := "[0-9]".regex,

  'BOM := "\357\273\277"

  ,
  'HexEntity := "&#[Xx]([0-9a-fA-F])+".regex ~ ";",
  'DecEntity := "&#([0-9])+".regex ~ ";",
  'CharEntity := "&([A-Za-z0-9])+".regex ~ ";",

  'NonindentSpace := "   " | "  " | " " | ""
  ,
  'Indent := "\t" | "    "
  ,
  'IndentedLine := 'Indent ~ 'Line
  ,
  'OptionallyIndentedLine := 'Indent.? ~ 'Line

  //   'StartList  'starts a  'list  'data  'structure  'that  'can  'be  'added  'to  'with  'cons:
  ,
  'StartList := "" // special functionality not implemented


  ,
  'Line := 'RawLine

  ,
  'RawLine := "[^\r\n]*".regex ~ 'Newline | ".+".regex ~ 'Eof

  ,
//  'SkipBlock := 'HtmlBlock | 'RawLine.rep(1) ~ 'BlankLine.rep | 'BlankLine.rep(1) | 'RawLine,

  //   'Syntax  'extensions

  'ExtendedSpecialChar := "." | "-" | "'" | "\"" | "^"

  ,
  'Smart := 'Ellipsis | 'Dash | 'SingleQuoted | 'DoubleQuoted | 'Apostrophe

  ,
  'Apostrophe := "'"


  ,
  'Ellipsis := "..." | ". . ."


  ,
  'Dash := 'EmDash | 'EnDash

  ,
  'EnDash := "-"


  ,
  'EmDash := "---" | "--"


  ,
  'SingleQuoteStart := "'"

  ,
  'SingleQuoteEnd := "'"

  ,
  'SingleQuoted := 'SingleQuoteStart ~ 'StartList ~ 'Inline.rep(1) ~ 'SingleQuoteEnd


  ,
  'DoubleQuoteStart := "\""

  ,
  'DoubleQuoteEnd := "\""

  ,
  'DoubleQuoted := 'DoubleQuoteStart ~ 'StartList ~ 'Inline.rep(1) ~ 'DoubleQuoteEnd


  ,
  'NoteReference :=
    'RawNoteReference

  ,
  'RawNoteReference := "[^" ~ "[^\n\r]\\]]+" ~ "]"


  ,
  'Note := 'NonindentSpace ~ 'RawNoteReference ~ ":" ~ 'Sp ~ 'StartList ~ 'RawNoteBlock ~ ('Indent ~ 'RawNoteBlock).rep

  ,
  'InlineNote := "^[" ~ 'StartList ~ 'Inline.rep(1) ~ "]"


  ,
//  'Notes := 'StartList ~ ('Note | 'SkipBlock).rep,


  'RawNoteBlock := 'StartList ~ 'OptionallyIndentedLine.rep(1) ~ 'BlankLine.rep


)
