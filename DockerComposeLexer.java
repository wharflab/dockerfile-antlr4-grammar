// Generated from DockerComposeLexer.g4 by ANTLR 4.13.2

    import org.antlr.v4.runtime.*;
    import org.antlr.v4.runtime.misc.Pair;
    import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DockerComposeLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COLON=1, DASH=2, LBRACKET=3, RBRACKET=4, COMMA=5, STRING=6, SCALAR=7, 
		COMMENT=8, WS=9, NEWLINE=10, INDENT=11, DEDENT=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COLON", "DASH", "LBRACKET", "RBRACKET", "COMMA", "STRING", "SCALAR", 
			"COMMENT", "WS", "NEWLINE", "INDENT", "DEDENT", "ANY_CHAR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "':'", "'-'", "'['", "']'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COLON", "DASH", "LBRACKET", "RBRACKET", "COMMA", "STRING", "SCALAR", 
			"COMMENT", "WS", "NEWLINE", "INDENT", "DEDENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	    private Deque<Integer> indentStack = new ArrayDeque<>();
	    private Queue<Token> tokenQueue = new LinkedList<>();
	    private boolean opened = false;

	    @Override
	    public Token nextToken() {
	        if (!opened) {
	            indentStack.push(0);
	            opened = true;
	        }

	        if (!tokenQueue.isEmpty()) {
	            return tokenQueue.poll();
	        }

	        Token t = super.nextToken();

	        if (t.getType() == NEWLINE) {
	            Token next = super.nextToken();
	            int indent = 0;
	            while (next.getType() == WS || next.getType() == NEWLINE) {
	                if (next.getType() == NEWLINE) {
	                    t = next;
	                    next = super.nextToken();
	                } else {
	                    indent = next.getText().length();
	                    next = super.nextToken();
	                }
	            }
	            
	            if (next.getType() == EOF) {
	                indent = 0;
	            }

	            int prevIndent = indentStack.peek();
	            if (indent > prevIndent) {
	                indentStack.push(indent);
	                tokenQueue.add(createToken(INDENT, "INDENT", t));
	            } else if (indent < prevIndent) {
	                while (!indentStack.isEmpty() && indentStack.peek() > indent) {
	                    indentStack.pop();
	                    tokenQueue.add(createToken(DEDENT, "DEDENT", t));
	                }
	            }
	            
	            tokenQueue.add(next);
	            return t;
	        } else if (t.getType() == EOF) {
	            while (!indentStack.isEmpty() && indentStack.peek() > 0) {
	                indentStack.pop();
	                tokenQueue.add(createToken(DEDENT, "DEDENT", t));
	            }
	            tokenQueue.add(t);
	            return tokenQueue.poll();
	        }

	        return t;
	    }

	    private Token createToken(int type, String text, Token parent) {
	        Pair<TokenSource, CharStream> source = new Pair<>(this, _input);
	        CommonToken t = new CommonToken(source, type, parent.getChannel(), parent.getStartIndex(), parent.getStopIndex());
	        t.setText(text);
	        t.setLine(parent.getLine());
	        t.setCharPositionInLine(parent.getCharPositionInLine());
	        return t;
	    }


	public DockerComposeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DockerComposeLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return COLON_sempred((RuleContext)_localctx, predIndex);
		case 1:
			return DASH_sempred((RuleContext)_localctx, predIndex);
		case 4:
			return COMMA_sempred((RuleContext)_localctx, predIndex);
		case 10:
			return INDENT_sempred((RuleContext)_localctx, predIndex);
		case 11:
			return DEDENT_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean COLON_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return  Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' ;
		}
		return true;
	}
	private boolean DASH_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return  Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' ;
		}
		return true;
	}
	private boolean COMMA_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return  Character.isWhitespace((char)_input.LA(1)) || _input.LA(1) == EOF || _input.LA(1) == '\r' || _input.LA(1) == '\n' ;
		}
		return true;
	}
	private boolean INDENT_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return  false ;
		}
		return true;
	}
	private boolean DEDENT_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return  false ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0000\fv\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0005\u0005-\b\u0005\n\u0005\f\u00050\t\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u00057\b"+
		"\u0005\n\u0005\f\u0005:\t\u0005\u0001\u0005\u0003\u0005=\b\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0004\u0006F\b\u0006\u000b\u0006\f\u0006G\u0001\u0007\u0001\u0007"+
		"\u0005\u0007L\b\u0007\n\u0007\f\u0007O\t\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0004\bT\b\b\u000b\b\f\bU\u0001\b\u0001\b\u0001\t\u0003\t[\b"+
		"\t\u0001\t\u0001\t\u0004\t_\b\t\u000b\t\f\t`\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0000\u0000\r\u0001\u0001\u0003\u0002\u0005\u0003\u0007"+
		"\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b"+
		"\u0017\f\u0019\u0000\u0001\u0000\u0006\u0002\u0000\"\"\\\\\u0002\u0000"+
		"\'\'\\\\\u0007\u0000\t\n\r\r  ##,-[[]]\u0003\u0000\t\n\r\r  \u0002\u0000"+
		"\n\n\r\r\u0002\u0000\t\t  \u0083\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0001\u001b\u0001"+
		"\u0000\u0000\u0000\u0003\u001e\u0001\u0000\u0000\u0000\u0005!\u0001\u0000"+
		"\u0000\u0000\u0007#\u0001\u0000\u0000\u0000\t%\u0001\u0000\u0000\u0000"+
		"\u000b<\u0001\u0000\u0000\u0000\rE\u0001\u0000\u0000\u0000\u000fI\u0001"+
		"\u0000\u0000\u0000\u0011S\u0001\u0000\u0000\u0000\u0013^\u0001\u0000\u0000"+
		"\u0000\u0015b\u0001\u0000\u0000\u0000\u0017j\u0001\u0000\u0000\u0000\u0019"+
		"r\u0001\u0000\u0000\u0000\u001b\u001c\u0005:\u0000\u0000\u001c\u001d\u0004"+
		"\u0000\u0000\u0000\u001d\u0002\u0001\u0000\u0000\u0000\u001e\u001f\u0005"+
		"-\u0000\u0000\u001f \u0004\u0001\u0001\u0000 \u0004\u0001\u0000\u0000"+
		"\u0000!\"\u0005[\u0000\u0000\"\u0006\u0001\u0000\u0000\u0000#$\u0005]"+
		"\u0000\u0000$\b\u0001\u0000\u0000\u0000%&\u0005,\u0000\u0000&\'\u0004"+
		"\u0004\u0002\u0000\'\n\u0001\u0000\u0000\u0000(.\u0005\"\u0000\u0000)"+
		"-\b\u0000\u0000\u0000*+\u0005\\\u0000\u0000+-\t\u0000\u0000\u0000,)\u0001"+
		"\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000-0\u0001\u0000\u0000\u0000"+
		".,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/1\u0001\u0000\u0000"+
		"\u00000.\u0001\u0000\u0000\u00001=\u0005\"\u0000\u000028\u0005\'\u0000"+
		"\u000037\b\u0001\u0000\u000045\u0005\\\u0000\u000057\t\u0000\u0000\u0000"+
		"63\u0001\u0000\u0000\u000064\u0001\u0000\u0000\u00007:\u0001\u0000\u0000"+
		"\u000086\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u00009;\u0001\u0000"+
		"\u0000\u0000:8\u0001\u0000\u0000\u0000;=\u0005\'\u0000\u0000<(\u0001\u0000"+
		"\u0000\u0000<2\u0001\u0000\u0000\u0000=\f\u0001\u0000\u0000\u0000>F\b"+
		"\u0002\u0000\u0000?@\u0005:\u0000\u0000@F\b\u0003\u0000\u0000AB\u0005"+
		"-\u0000\u0000BF\b\u0003\u0000\u0000CD\u0005,\u0000\u0000DF\b\u0003\u0000"+
		"\u0000E>\u0001\u0000\u0000\u0000E?\u0001\u0000\u0000\u0000EA\u0001\u0000"+
		"\u0000\u0000EC\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GE\u0001"+
		"\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000H\u000e\u0001\u0000\u0000"+
		"\u0000IM\u0005#\u0000\u0000JL\b\u0004\u0000\u0000KJ\u0001\u0000\u0000"+
		"\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000"+
		"\u0000\u0000NP\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000PQ\u0006"+
		"\u0007\u0000\u0000Q\u0010\u0001\u0000\u0000\u0000RT\u0007\u0005\u0000"+
		"\u0000SR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000US\u0001\u0000"+
		"\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0006"+
		"\b\u0001\u0000X\u0012\u0001\u0000\u0000\u0000Y[\u0005\r\u0000\u0000ZY"+
		"\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000"+
		"\u0000\\_\u0005\n\u0000\u0000]_\u0005\r\u0000\u0000^Z\u0001\u0000\u0000"+
		"\u0000^]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`^\u0001\u0000"+
		"\u0000\u0000`a\u0001\u0000\u0000\u0000a\u0014\u0001\u0000\u0000\u0000"+
		"bc\u0004\n\u0003\u0000cd\u0005I\u0000\u0000de\u0005N\u0000\u0000ef\u0005"+
		"D\u0000\u0000fg\u0005E\u0000\u0000gh\u0005N\u0000\u0000hi\u0005T\u0000"+
		"\u0000i\u0016\u0001\u0000\u0000\u0000jk\u0004\u000b\u0004\u0000kl\u0005"+
		"D\u0000\u0000lm\u0005E\u0000\u0000mn\u0005D\u0000\u0000no\u0005E\u0000"+
		"\u0000op\u0005N\u0000\u0000pq\u0005T\u0000\u0000q\u0018\u0001\u0000\u0000"+
		"\u0000rs\t\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0006\f\u0002"+
		"\u0000u\u001a\u0001\u0000\u0000\u0000\r\u0000,.68<EGMUZ^`\u0003\u0006"+
		"\u0000\u0000\u0000\u0001\u0000\u0007\u0007\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}