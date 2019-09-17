//  Translated  from  https://github.com/jgm/peg-markdown/blob/master/markdown_parser.leg

grammar Markdown;


  doc : bOM?  block*;

  block : blankLine*
    (blockQuote
      | verbatim
      | note
      | reference
      | horizontalRule
      | heading
      | orderedList
      | bulletList
      | htmlBlock
      | styleBlock
      | para
      | plain)

;

  para : nonindentSpace inlines blankLine+


;
  plain : inlines

;
  atxInline : inline
;

  atxHeading : ('######' | '#####' | '####' | '###' | '##' | '#') sp  atxInline+ (sp '#'+ sp)? Newline

;
  setextHeading : setextHeading1 | setextHeading2

;
  SetextBottom1 : '='+ Newline

;
  SetextBottom2 : '-'+ Newline

;
  setextHeading1 :  inline+ sp Newline SetextBottom1

;
  setextHeading2 :  inline+ sp Newline SetextBottom2

;
  heading : setextHeading | atxHeading

;
  blockQuote : blockQuoteRaw


;
  blockQuoteRaw :  ('>' ' '? line line* blankLine*)+


;
  nonblankIndentedLine : indentedLine

;
  verbatimChunk :  blankLine* nonblankIndentedLine+


;
  verbatim :  verbatimChunk+


;
  horizontalRule : nonindentSpace
    ('*' sp '*' sp '*' (sp '*')*
      | '-' sp '-' sp '-' (sp '-')*
      | '_' sp '_' sp '_' (sp '_')*)
    sp Newline blankLine+


;
  bullet : nonindentSpace ('+' | '*' | '-') spacechar+

;
  bulletList : (listTight | listLoose)


;
  listTight :  listItemTight+ blankLine*

;
  listLoose :  (listItem blankLine*)+


;
  listItem : (bullet | Enumerator)  listBlock listContinuationBlock*;


  listItemTight : (bullet | Enumerator)  listBlock listContinuationBlock*


;
  listBlock :  line listBlockLine*


;
  listContinuationBlock :  blankLine* (Indent listBlock)+


;
  Enumerator : NonindentSpace [0-9]+ '.' Spacechar+

;
  orderedList : listTight | listLoose


;
  listBlockLine :


    optionallyIndentedLine

  //   parsers  for  different  kinds  of  block-level  hTML  content.
  //   this  is  repetitive  due  to  constraints  of  pEG  grammar.

;
  htmlBlockOpenAddress : '<' spnl ('address' | 'ADDRESS') spnl HtmlAttribute* '>'
;
  htmlBlockCloseAddress : '<' spnl '/' ('address' | 'ADDRESS') spnl '>'
;
  htmlBlockAddress : htmlBlockOpenAddress htmlBlockAddress* htmlBlockCloseAddress

;
  htmlBlockOpenBlockquote : '<' spnl ('blockquote' | 'BLOCKQUOTE') spnl HtmlAttribute* '>'
;
  htmlBlockCloseBlockquote : '<' spnl '/' ('blockquote' | 'BLOCKQUOTE') spnl '>'
;
  htmlBlockBlockquote : htmlBlockOpenBlockquote htmlBlockBlockquote* htmlBlockCloseBlockquote

;
  htmlBlockOpenCenter : '<' spnl ('center' | 'CENTER') spnl HtmlAttribute* '>'
;
  htmlBlockCloseCenter : '<' spnl '/' ('center' | 'CENTER') spnl '>'
;
  htmlBlockCenter : htmlBlockOpenCenter htmlBlockCenter* htmlBlockCloseCenter

;
  htmlBlockOpenDir : '<' spnl ('dir' | 'DIR') spnl HtmlAttribute* '>'
;
  htmlBlockCloseDir : '<' spnl '/' ('dir' | 'DIR') spnl '>'
;
  htmlBlockDir : htmlBlockOpenDir htmlBlockDir* htmlBlockCloseDir

;
  htmlBlockOpenDiv : '<' spnl ('div' | 'DIV') spnl HtmlAttribute* '>'
;
  htmlBlockCloseDiv : '<' spnl '/' ('div' | 'DIV') spnl '>'
;
  htmlBlockDiv : htmlBlockOpenDiv htmlBlockDiv* htmlBlockCloseDiv

;
  htmlBlockOpenDl : '<' spnl ('dl' | 'DL') spnl HtmlAttribute* '>'
;
  htmlBlockCloseDl : '<' spnl '/' ('dl' | 'DL') spnl '>'
;
  htmlBlockDl : htmlBlockOpenDl htmlBlockDl* htmlBlockCloseDl

;
  htmlBlockOpenFieldset : '<' spnl ('fieldset' | 'FIELDSET') spnl HtmlAttribute* '>'
;
  htmlBlockCloseFieldset : '<' spnl '/' ('fieldset' | 'FIELDSET') spnl '>'
;
  htmlBlockFieldset : htmlBlockOpenFieldset htmlBlockFieldset* htmlBlockCloseFieldset

;
  htmlBlockOpenForm : '<' spnl ('form' | 'FORM') spnl HtmlAttribute* '>'
;
  htmlBlockCloseForm : '<' spnl '/' ('form' | 'FORM') spnl '>'
;
  htmlBlockForm : htmlBlockOpenForm htmlBlockForm* htmlBlockCloseForm

;
  htmlBlockOpenH1 : '<' spnl ('h1' | 'H1') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH1 : '<' spnl '/' ('h1' | 'H1') spnl '>'
;
  htmlBlockH1 : htmlBlockOpenH1 htmlBlockH1* htmlBlockCloseH1

;
  htmlBlockOpenH2 : '<' spnl ('h2' | 'H2') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH2 : '<' spnl '/' ('h2' | 'H2') spnl '>'
;
  htmlBlockH2 : htmlBlockOpenH2 htmlBlockH2* htmlBlockCloseH2

;
  htmlBlockOpenH3 : '<' spnl ('h3' | 'H3') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH3 : '<' spnl '/' ('h3' | 'H3') spnl '>'
;
  htmlBlockH3 : htmlBlockOpenH3 htmlBlockH3* htmlBlockCloseH3

;
  htmlBlockOpenH4 : '<' spnl ('h4' | 'H4') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH4 : '<' spnl '/' ('h4' | 'H4') spnl '>'
;
  htmlBlockH4 : htmlBlockOpenH4 htmlBlockH4* htmlBlockCloseH4

;
  htmlBlockOpenH5 : '<' spnl ('h5' | 'H5') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH5 : '<' spnl '/' ('h5' | 'H5') spnl '>'
;
  htmlBlockH5 : htmlBlockOpenH5 htmlBlockH5* htmlBlockCloseH5

;
  htmlBlockOpenH6 : '<' spnl ('h6' | 'H6') spnl HtmlAttribute* '>'
;
  htmlBlockCloseH6 : '<' spnl '/' ('h6' | 'H6') spnl '>'
;
  htmlBlockH6 : htmlBlockOpenH6 htmlBlockH6* htmlBlockCloseH6

;
  htmlBlockOpenMenu : '<' spnl ('menu' | 'MENU') spnl HtmlAttribute* '>'
;
  htmlBlockCloseMenu : '<' spnl '/' ('menu' | 'MENU') spnl '>'
;
  htmlBlockMenu : htmlBlockOpenMenu htmlBlockMenu* htmlBlockCloseMenu

;
  htmlBlockOpenNoframes : '<' spnl ('noframes' | 'NOFRAMES') spnl HtmlAttribute* '>'
;
  htmlBlockCloseNoframes : '<' spnl '/' ('noframes' | 'NOFRAMES') spnl '>'
;
  htmlBlockNoframes : htmlBlockOpenNoframes htmlBlockNoframes* htmlBlockCloseNoframes

;
  htmlBlockOpenNoscript : '<' spnl ('noscript' | 'NOSCRIPT') spnl HtmlAttribute* '>'
;
  htmlBlockCloseNoscript : '<' spnl '/' ('noscript' | 'NOSCRIPT') spnl '>'
;
  htmlBlockNoscript : htmlBlockOpenNoscript htmlBlockNoscript* htmlBlockCloseNoscript

;
  htmlBlockOpenOl : '<' spnl ('ol' | 'OL') spnl HtmlAttribute* '>'
;
  htmlBlockCloseOl : '<' spnl '/' ('ol' | 'OL') spnl '>'
;
  htmlBlockOl : htmlBlockOpenOl htmlBlockOl* htmlBlockCloseOl

;
  htmlBlockOpenP : '<' spnl ('p' | 'P') spnl HtmlAttribute* '>'
;
  htmlBlockCloseP : '<' spnl '/' ('p' | 'P') spnl '>'
;
  htmlBlockP : htmlBlockOpenP htmlBlockP* htmlBlockCloseP

;
  htmlBlockOpenPre : '<' spnl ('pre' | 'PRE') spnl HtmlAttribute* '>'
;
  htmlBlockClosePre : '<' spnl '/' ('pre' | 'PRE') spnl '>'
;
  htmlBlockPre : htmlBlockOpenPre htmlBlockPre* htmlBlockClosePre

;
  htmlBlockOpenTable : '<' spnl ('table' | 'TABLE') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTable : '<' spnl '/' ('table' | 'TABLE') spnl '>'
;
  htmlBlockTable : htmlBlockOpenTable htmlBlockTable* htmlBlockCloseTable

;
  htmlBlockOpenUl : '<' spnl ('ul' | 'UL') spnl HtmlAttribute* '>'
;
  htmlBlockCloseUl : '<' spnl '/' ('ul' | 'UL') spnl '>'
;
  htmlBlockUl : htmlBlockOpenUl htmlBlockUl* htmlBlockCloseUl

;
  htmlBlockOpenDd : '<' spnl ('dd' | 'DD') spnl HtmlAttribute* '>'
;
  htmlBlockCloseDd : '<' spnl '/' ('dd' | 'DD') spnl '>'
;
  htmlBlockDd : htmlBlockOpenDd htmlBlockDd* htmlBlockCloseDd

;
  htmlBlockOpenDt : '<' spnl ('dt' | 'DT') spnl HtmlAttribute* '>'
;
  htmlBlockCloseDt : '<' spnl '/' ('dt' | 'DT') spnl '>'
;
  htmlBlockDt : htmlBlockOpenDt htmlBlockDt* htmlBlockCloseDt

;
  htmlBlockOpenFrameset : '<' spnl ('frameset' | 'FRAMESET') spnl HtmlAttribute* '>'
;
  htmlBlockCloseFrameset : '<' spnl '/' ('frameset' | 'FRAMESET') spnl '>'
;
  htmlBlockFrameset : htmlBlockOpenFrameset htmlBlockFrameset* htmlBlockCloseFrameset

;
  htmlBlockOpenLi : '<' spnl ('li' | 'LI') spnl HtmlAttribute* '>'
;
  htmlBlockCloseLi : '<' spnl '/' ('li' | 'LI') spnl '>'
;
  htmlBlockLi : htmlBlockOpenLi htmlBlockLi* htmlBlockCloseLi

;
  htmlBlockOpenTbody : '<' spnl ('tbody' | 'TBODY') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTbody : '<' spnl '/' ('tbody' | 'TBODY') spnl '>'
;
  htmlBlockTbody : htmlBlockOpenTbody htmlBlockTbody* htmlBlockCloseTbody

;
  htmlBlockOpenTd : '<' spnl ('td' | 'TD') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTd : '<' spnl '/' ('td' | 'TD') spnl '>'
;
  htmlBlockTd : htmlBlockOpenTd htmlBlockTd* htmlBlockCloseTd

;
  htmlBlockOpenTfoot : '<' spnl ('tfoot' | 'TFOOT') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTfoot : '<' spnl '/' ('tfoot' | 'TFOOT') spnl '>'
;
  htmlBlockTfoot : htmlBlockOpenTfoot htmlBlockTfoot* htmlBlockCloseTfoot

;
  htmlBlockOpenTh : '<' spnl ('th' | 'TH') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTh : '<' spnl '/' ('th' | 'TH') spnl '>'
;
  htmlBlockTh : htmlBlockOpenTh htmlBlockTh* htmlBlockCloseTh

;
  htmlBlockOpenThead : '<' spnl ('thead' | 'THEAD') spnl HtmlAttribute* '>'
;
  htmlBlockCloseThead : '<' spnl '/' ('thead' | 'THEAD') spnl '>'
;
  htmlBlockThead : htmlBlockOpenThead htmlBlockThead* htmlBlockCloseThead

;
  htmlBlockOpenTr : '<' spnl ('tr' | 'TR') spnl HtmlAttribute* '>'
;
  htmlBlockCloseTr : '<' spnl '/' ('tr' | 'TR') spnl '>'
;
  htmlBlockTr : htmlBlockOpenTr htmlBlockTr* htmlBlockCloseTr

;
  htmlBlockOpenScript : '<' spnl ('script' | 'SCRIPT') spnl HtmlAttribute* '>'
;
  htmlBlockCloseScript : '<' spnl '/' ('script' | 'SCRIPT') spnl '>'
;
  htmlBlockScript : htmlBlockOpenScript htmlBlockCloseScript

;
  htmlBlockOpenHead : '<' spnl ('head' | 'HEAD') spnl HtmlAttribute* '>'
;
  htmlBlockCloseHead : '<' spnl '/' ('head' | 'HEAD') spnl '>'
;
  htmlBlockHead : htmlBlockOpenHead htmlBlockCloseHead

;
  htmlBlockInTags : htmlBlockAddress
    | htmlBlockBlockquote
    | htmlBlockCenter
    | htmlBlockDir
    | htmlBlockDiv
    | htmlBlockDl
    | htmlBlockFieldset
    | htmlBlockForm
    | htmlBlockH1
    | htmlBlockH2
    | htmlBlockH3
    | htmlBlockH4
    | htmlBlockH5
    | htmlBlockH6
    | htmlBlockMenu
    | htmlBlockNoframes
    | htmlBlockNoscript
    | htmlBlockOl
    | htmlBlockP
    | htmlBlockPre
    | htmlBlockTable
    | htmlBlockUl
    | htmlBlockDd
    | htmlBlockDt
    | htmlBlockFrameset
    | htmlBlockLi
    | htmlBlockTbody
    | htmlBlockTd
    | htmlBlockTfoot
    | htmlBlockTh
    | htmlBlockThead
    | htmlBlockTr
    | htmlBlockScript
    | htmlBlockHead

;
  htmlBlock : (htmlBlockInTags | HtmlComment | htmlBlockSelfClosing) blankLine+


;
  htmlBlockSelfClosing : '<' spnl htmlBlockType spnl HtmlAttribute* '/' spnl '>'

;
  htmlBlockType : 'address' | 'blockquote' | 'center' | 'dir' | 'div' | 'dl' | 'fieldset' | 'form' | 'h1' | 'h2' | 'h3' |
    'h4' | 'h5' | 'h6' | 'hr' | 'isindex' | 'menu' | 'noframes' | 'noscript' | 'ol' | 'p' | 'pre' | 'table' |
    'ul' | 'dd' | 'dt' | 'frameset' | 'li' | 'tbody' | 'td' | 'tfoot' | 'th' | 'thead' | 'tr' | 'script' |
    'ADDRESS' | 'BLOCKQUOTE' | 'CENTER' | 'DIR' | 'DIV' | 'DL' | 'FIELDSET' | 'FORM' | 'H1' | 'H2' | 'H3' |
    'H4' | 'H5' | 'H6' | 'HR' | 'ISINDEX' | 'MENU' | 'NOFRAMES' | 'NOSCRIPT' | 'OL' | 'P' | 'PRE' | 'TABLE' |
    'UL' | 'DD' | 'DT' | 'FRAMESET' | 'LI' | 'TBODY' | 'TD' | 'TFOOT' | 'TH' | 'THEAD' | 'TR' | 'SCRIPT'

;
  styleOpen : '<' spnl ('style' | 'STYLE') spnl HtmlAttribute* '>'
;
  styleClose : '<' spnl '/' ('style' | 'STYLE') spnl '>'
;
  inStyleTags : styleOpen styleClose
;
  styleBlock : inStyleTags blankLine*


;
  inlines :  (inline
    | endline inline)+ endline?


;
  inline : str
    | endline
    | ulOrStarLine
    | SPace
    | strong
    | emph
    | strike
    | image
    | link
    | noteReference
    | inlineNote
    | Code
    | rawHtml
    | entity
    | EscapedChar
    | smart
    | symbol

;
  SPace : Spacechar+


;
  str :  NormalChar+ StrChunk*


;
  StrChunk : (NormalChar | '_'+ Alphanumeric)+ | AposChunk

;
  AposChunk : '\'' Alphanumeric


;
  EscapedChar : '\\' [\-\\`|*_{}[\]()# +.!><]


;
  entity : (HexEntity | DecEntity | CharEntity)


;
  endline : lineBreak | terminalEndline | normalEndline

;
  normalEndline : sp Newline


;
  terminalEndline : sp Newline EOF


;
  lineBreak : '  ' normalEndline


;
  symbol : SpecialChar


  //   this  keeps  the  parser  from  getting  bogged  down  on  long  strings  of *  or _,
  //   or  strings  of *  or _  with  SPace  on  each  side:
;
  ulOrStarLine : (ulLine | starLine)
;
  starLine : '****' '*'* | spacechar '*'+ spacechar
;
  ulLine : '____' '_'* | spacechar '_'+ spacechar

;
  emph : emphStar | emphUl

;
//  whiteSPace : SPacechar | newline;

  emphStar : '*'  (inline | strongStar)+ '*';

  emphUl : '_'  (inline | strongUl)+ '_';


  strong : strongStar | strongUl

;
  strongStar : '**'  inline+ '**';


  strongUl : '__'  inline+ '__'

;
  strike : '~~'  inline+ '~~'


;
  image : '!' (explicitLink | referenceLink)

;
  link : explicitLink | referenceLink | AutoLink

;
  referenceLink : referenceLinkDouble | referenceLinkSingle

;
  referenceLinkDouble : label spnl label

;
  referenceLinkSingle : label (spnl '[]')?


;
  explicitLink : label '(' sp ('<' sourceContents '>' | sourceContents) spnl (TitleSingle | TitleDouble )? sp ')'


;
  sourceContents : (NonSpaceChar+ | '(' sourceContents ')')*

;

  TitleSingle : '\'' [^\n\r ')]* '\''

;
  TitleDouble : '"'  [^\n\r ")]* '"'

;
  AutoLink : AutoLinkUrl | AutoLinkEmail

;
  AutoLinkUrl : '<' [A-Za-z]+ '://' [^>\n\r]+ '>'


;
  AutoLinkEmail : '<' 'mailto:'? [-A-Za-z0-9.\\+_/!%~$]+ '@' [^>\n\r]+ '>'


;
  reference : nonindentSpace label ':' spnl RefSrc refTitle blankLine+


;
  label : '['  inline* ']'

;
  RefSrc : NonSpaceChar+


;
  refTitle : (RefTitleSingle | RefTitleDouble | RefTitleParens)?

;
  RefTitleSingle : Spnl '\'' [' \n\r]* '\''

;
  RefTitleDouble : Spnl '"' [" \n\r]* '"'

;
  RefTitleParens : Spnl '(' [) \n\r]* ')'

;
//  references :  (reference | skipBlock)*;


  fragment Ticks1 : '`'
;
  fragment Ticks2 : '``'
;
  fragment Ticks3 : '```'
;
  fragment Ticks4 : '````'
;
  fragment Ticks5 : '`````'

;
  Code : Ticks1 SP (NonSpaceChar+ | '`'+ | Spacechar | Newline)+ SP Ticks1
    | Ticks2 SP (NonSpaceChar+ | '`'+ | Spacechar | Newline)+ SP Ticks2
    | Ticks3 SP (NonSpaceChar+ | '`'+ | Spacechar | Newline)+ SP Ticks3
    | Ticks4 SP (NonSpaceChar+ | '`'+ | Spacechar | Newline)+ SP Ticks4
    | Ticks5 SP (NonSpaceChar+ | '`'+ | Spacechar | Newline)+ SP Ticks5
;
  rawHtml : HtmlComment | htmlBlockScript | HtmlTag

;
  blankLine : sp Newline

;
  Quoted : '"' [^"]* '"' | '\'' [^']* '\''
;
  HtmlAttribute : (AlphanumericAscii | '-')+ Spnl ('=' Spnl (Quoted | NonSpaceChar+))? Spnl
;
  HtmlComment : '<!--' .*? '-->'
;
  HtmlTag : '<' Spnl '/'? AlphanumericAscii+ Spnl HtmlAttribute* '/'? Spnl '>'
;
  spacechar: ' ' | '\t';

  fragment Spacechar : ' ' | '\t'
;
  NonSpaceChar : [^ \t\r\n]
;
  Newline : '\n' | '\r' '\n'?
;
  sp: spacechar*;

  fragment SP : Spacechar*
;
  spnl : sp (Newline sp)?;

  fragment Spnl : SP (Newline SP)?;

  SpecialChar : '~' | '*' | '_' | '`' | '&' | '[' | ']' | '(' | ')' | '<' | '!' | '#' | '\\' | '\'' | '"' | ExtendedSpecialChar
;
  NormalChar : ~ ('~' | '*' | '_' | '`' | '&' | '[' | ']' | '(' | ')' | '<' | '!' | '#' | '\\' | '"' | '.' | '-' | '\'' | '^' | ' ' | '\t' | '\r' | '\n' )
;
  fragment Alphanumeric : [0-9A-Za-z] | '\u0200' | '\u0201' | '\u0202' | '\u0203' | '\u0204' | '\u0205' | '\u0206' | '\u0207' | '\u0210' | '\u0211' | '\u0212' | '\u0213' | '\u0214'
    | '\u0215' | '\u0216' | '\u0217' | '\u0220' | '\u0221' | '\u0222' | '\u0223' | '\u0224' | '\u0225' | '\u0226' | '\u0227' | '\u0230' | '\u0231' | '\u0232' | '\u0233' | '\u0234' | '\u0235'
    | '\u0236' | '\u0237' | '\u0240' | '\u0241' | '\u0242' | '\u0243' | '\u0244' | '\u0245' | '\u0246' | '\u0247' | '\u0250' | '\u0251' | '\u0252' | '\u0253' | '\u0254' | '\u0255' | '\u0256'
    | '\u0257' | '\u0260' | '\u0261' | '\u0262' | '\u0263' | '\u0264' | '\u0265' | '\u0266' | '\u0267' | '\u0270' | '\u0271' | '\u0272' | '\u0273' | '\u0274' | '\u0275' | '\u0276' | '\u0277'
    | '\u0300' | '\u0301' | '\u0302' | '\u0303' | '\u0304' | '\u0305' | '\u0306' | '\u0307' | '\u0310' | '\u0311' | '\u0312' | '\u0313' | '\u0314' | '\u0315' | '\u0316' | '\u0317' | '\u0320'
    | '\u0321' | '\u0322' | '\u0323' | '\u0324' | '\u0325' | '\u0326' | '\u0327' | '\u0330' | '\u0331' | '\u0332' | '\u0333' | '\u0334' | '\u0335' | '\u0336' | '\u0337' | '\u0340' | '\u0341'
    | '\u0342' | '\u0343' | '\u0344' | '\u0345' | '\u0346' | '\u0347' | '\u0350' | '\u0351' | '\u0352' | '\u0353' | '\u0354' | '\u0355' | '\u0356' | '\u0357' | '\u0360' | '\u0361' | '\u0362'
    | '\u0363' | '\u0364' | '\u0365' | '\u0366' | '\u0367' | '\u0370' | '\u0371' | '\u0372' | '\u0373' | '\u0374' | '\u0375' | '\u0376' | '\u0377'
;
  fragment AlphanumericAscii : [A-Za-z0-9]
;
//  Digit : [0-9];

  bOM : '\u0357\u0273\u0277'

;
  HexEntity : '&' '#' [Xx] [0-9a-fA-F]+ ';';
  DecEntity : '&' '#' [0-9]+ ';';
  CharEntity : '&' [A-Za-z0-9]+ ';';

  nonindentSpace : ('   ' | '  ' | ' ')?;

  fragment NonindentSpace : ('   ' | '  ' | ' ')?
;
  Indent : '\t' | '    '
;
  indentedLine : Indent line
;
  optionallyIndentedLine : Indent? line

;
  line : RawLine

;
  RawLine : [^\r\n]* Newline | .+? EOF

;
//  skipBlock : htmlBlock | rawLine+ blankLine* | blankLine+ | rawLine;

  //   syntax  extensions

  ExtendedSpecialChar : '.' | '-' | '\'' | '"' | '^'

;
  smart : Ellipsis | Dash | singleQuoted | doubleQuoted | '\''

;
  Ellipsis : '...' | '. . .'


;
  Dash : EmDash | EnDash

;
  EnDash : '-'


;
  EmDash : '---' | '--'


;
  singleQuoted : '\''  inline+ '\''

;
  doubleQuoted : '"'  inline+ '"'


;
  noteReference :
    RawNoteReference

;
  RawNoteReference : '[^' [^\n\r\]]+ ']'


;
  note : nonindentSpace RawNoteReference ':' sp  rawNoteBlock (Indent rawNoteBlock)*

;
  inlineNote : '^['  inline+ ']'


;
//  notes :=  (note | skipBlock)*,


  rawNoteBlock :  optionallyIndentedLine+ blankLine*

;
