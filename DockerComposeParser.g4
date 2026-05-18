parser grammar DockerComposeParser;

options { tokenVocab=DockerComposeLexer; }

composeFile: (element | NEWLINE)* EOF;

element: pair | listItem;

pair: key COLON (value | nested_block | NEWLINE);

key: SCALAR | STRING;

value: SCALAR | STRING | flow_list;

nested_block: NEWLINE INDENT (element | NEWLINE)+ DEDENT;

listItem: DASH (value | nested_block | NEWLINE);

flow_list: LBRACKET (value (COMMA value)*)? RBRACKET;
