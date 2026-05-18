// Generated from DockerComposeParser.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DockerComposeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COLON=1, DASH=2, LBRACKET=3, RBRACKET=4, COMMA=5, STRING=6, SCALAR=7, 
		COMMENT=8, WS=9, NEWLINE=10, INDENT=11, DEDENT=12;
	public static final int
		RULE_composeFile = 0, RULE_element = 1, RULE_pair = 2, RULE_key = 3, RULE_value = 4, 
		RULE_nested_block = 5, RULE_listItem = 6, RULE_flow_list = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"composeFile", "element", "pair", "key", "value", "nested_block", "listItem", 
			"flow_list"
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

	@Override
	public String getGrammarFileName() { return "DockerComposeParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DockerComposeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComposeFileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(DockerComposeParser.EOF, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(DockerComposeParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(DockerComposeParser.NEWLINE, i);
		}
		public ComposeFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_composeFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterComposeFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitComposeFile(this);
		}
	}

	public final ComposeFileContext composeFile() throws RecognitionException {
		ComposeFileContext _localctx = new ComposeFileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_composeFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1220L) != 0)) {
				{
				setState(18);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case DASH:
				case STRING:
				case SCALAR:
					{
					setState(16);
					element();
					}
					break;
				case NEWLINE:
					{
					setState(17);
					match(NEWLINE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(23);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElementContext extends ParserRuleContext {
		public PairContext pair() {
			return getRuleContext(PairContext.class,0);
		}
		public ListItemContext listItem() {
			return getRuleContext(ListItemContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_element);
		try {
			setState(27);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
			case SCALAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(25);
				pair();
				}
				break;
			case DASH:
				enterOuterAlt(_localctx, 2);
				{
				setState(26);
				listItem();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PairContext extends ParserRuleContext {
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public TerminalNode COLON() { return getToken(DockerComposeParser.COLON, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public Nested_blockContext nested_block() {
			return getRuleContext(Nested_blockContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(DockerComposeParser.NEWLINE, 0); }
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitPair(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			key();
			setState(30);
			match(COLON);
			setState(34);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(31);
				value();
				}
				break;
			case 2:
				{
				setState(32);
				nested_block();
				}
				break;
			case 3:
				{
				setState(33);
				match(NEWLINE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KeyContext extends ParserRuleContext {
		public TerminalNode SCALAR() { return getToken(DockerComposeParser.SCALAR, 0); }
		public TerminalNode STRING() { return getToken(DockerComposeParser.STRING, 0); }
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_key);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==SCALAR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValueContext extends ParserRuleContext {
		public TerminalNode SCALAR() { return getToken(DockerComposeParser.SCALAR, 0); }
		public TerminalNode STRING() { return getToken(DockerComposeParser.STRING, 0); }
		public Flow_listContext flow_list() {
			return getRuleContext(Flow_listContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_value);
		try {
			setState(41);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SCALAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(38);
				match(SCALAR);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(39);
				match(STRING);
				}
				break;
			case LBRACKET:
				enterOuterAlt(_localctx, 3);
				{
				setState(40);
				flow_list();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Nested_blockContext extends ParserRuleContext {
		public List<TerminalNode> NEWLINE() { return getTokens(DockerComposeParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(DockerComposeParser.NEWLINE, i);
		}
		public TerminalNode INDENT() { return getToken(DockerComposeParser.INDENT, 0); }
		public TerminalNode DEDENT() { return getToken(DockerComposeParser.DEDENT, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public Nested_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nested_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterNested_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitNested_block(this);
		}
	}

	public final Nested_blockContext nested_block() throws RecognitionException {
		Nested_blockContext _localctx = new Nested_blockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_nested_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(NEWLINE);
			setState(44);
			match(INDENT);
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(47);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case DASH:
				case STRING:
				case SCALAR:
					{
					setState(45);
					element();
					}
					break;
				case NEWLINE:
					{
					setState(46);
					match(NEWLINE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1220L) != 0) );
			setState(51);
			match(DEDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ListItemContext extends ParserRuleContext {
		public TerminalNode DASH() { return getToken(DockerComposeParser.DASH, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public Nested_blockContext nested_block() {
			return getRuleContext(Nested_blockContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(DockerComposeParser.NEWLINE, 0); }
		public ListItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterListItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitListItem(this);
		}
	}

	public final ListItemContext listItem() throws RecognitionException {
		ListItemContext _localctx = new ListItemContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_listItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(DASH);
			setState(57);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(54);
				value();
				}
				break;
			case 2:
				{
				setState(55);
				nested_block();
				}
				break;
			case 3:
				{
				setState(56);
				match(NEWLINE);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Flow_listContext extends ParserRuleContext {
		public TerminalNode LBRACKET() { return getToken(DockerComposeParser.LBRACKET, 0); }
		public TerminalNode RBRACKET() { return getToken(DockerComposeParser.RBRACKET, 0); }
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DockerComposeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DockerComposeParser.COMMA, i);
		}
		public Flow_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flow_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).enterFlow_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DockerComposeParserListener ) ((DockerComposeParserListener)listener).exitFlow_list(this);
		}
	}

	public final Flow_listContext flow_list() throws RecognitionException {
		Flow_listContext _localctx = new Flow_listContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flow_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(LBRACKET);
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 200L) != 0)) {
				{
				setState(60);
				value();
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(61);
					match(COMMA);
					setState(62);
					value();
					}
					}
					setState(67);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(70);
			match(RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\fI\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0005\u0000\u0013\b\u0000\n\u0000\f\u0000\u0016\t\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u001c\b\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"#\b\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004*\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0004\u00050\b\u0005\u000b\u0005\f\u00051\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006:\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007@\b\u0007\n\u0007"+
		"\f\u0007C\t\u0007\u0003\u0007E\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0000\u0000\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0001\u0001\u0000"+
		"\u0006\u0007M\u0000\u0014\u0001\u0000\u0000\u0000\u0002\u001b\u0001\u0000"+
		"\u0000\u0000\u0004\u001d\u0001\u0000\u0000\u0000\u0006$\u0001\u0000\u0000"+
		"\u0000\b)\u0001\u0000\u0000\u0000\n+\u0001\u0000\u0000\u0000\f5\u0001"+
		"\u0000\u0000\u0000\u000e;\u0001\u0000\u0000\u0000\u0010\u0013\u0003\u0002"+
		"\u0001\u0000\u0011\u0013\u0005\n\u0000\u0000\u0012\u0010\u0001\u0000\u0000"+
		"\u0000\u0012\u0011\u0001\u0000\u0000\u0000\u0013\u0016\u0001\u0000\u0000"+
		"\u0000\u0014\u0012\u0001\u0000\u0000\u0000\u0014\u0015\u0001\u0000\u0000"+
		"\u0000\u0015\u0017\u0001\u0000\u0000\u0000\u0016\u0014\u0001\u0000\u0000"+
		"\u0000\u0017\u0018\u0005\u0000\u0000\u0001\u0018\u0001\u0001\u0000\u0000"+
		"\u0000\u0019\u001c\u0003\u0004\u0002\u0000\u001a\u001c\u0003\f\u0006\u0000"+
		"\u001b\u0019\u0001\u0000\u0000\u0000\u001b\u001a\u0001\u0000\u0000\u0000"+
		"\u001c\u0003\u0001\u0000\u0000\u0000\u001d\u001e\u0003\u0006\u0003\u0000"+
		"\u001e\"\u0005\u0001\u0000\u0000\u001f#\u0003\b\u0004\u0000 #\u0003\n"+
		"\u0005\u0000!#\u0005\n\u0000\u0000\"\u001f\u0001\u0000\u0000\u0000\" "+
		"\u0001\u0000\u0000\u0000\"!\u0001\u0000\u0000\u0000#\u0005\u0001\u0000"+
		"\u0000\u0000$%\u0007\u0000\u0000\u0000%\u0007\u0001\u0000\u0000\u0000"+
		"&*\u0005\u0007\u0000\u0000\'*\u0005\u0006\u0000\u0000(*\u0003\u000e\u0007"+
		"\u0000)&\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000)(\u0001\u0000"+
		"\u0000\u0000*\t\u0001\u0000\u0000\u0000+,\u0005\n\u0000\u0000,/\u0005"+
		"\u000b\u0000\u0000-0\u0003\u0002\u0001\u0000.0\u0005\n\u0000\u0000/-\u0001"+
		"\u0000\u0000\u0000/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000"+
		"1/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000"+
		"\u000034\u0005\f\u0000\u00004\u000b\u0001\u0000\u0000\u000059\u0005\u0002"+
		"\u0000\u00006:\u0003\b\u0004\u00007:\u0003\n\u0005\u00008:\u0005\n\u0000"+
		"\u000096\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u000098\u0001\u0000"+
		"\u0000\u0000:\r\u0001\u0000\u0000\u0000;D\u0005\u0003\u0000\u0000<A\u0003"+
		"\b\u0004\u0000=>\u0005\u0005\u0000\u0000>@\u0003\b\u0004\u0000?=\u0001"+
		"\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000"+
		"AB\u0001\u0000\u0000\u0000BE\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000"+
		"\u0000D<\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000"+
		"\u0000\u0000FG\u0005\u0004\u0000\u0000G\u000f\u0001\u0000\u0000\u0000"+
		"\n\u0012\u0014\u001b\")/19AD";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}