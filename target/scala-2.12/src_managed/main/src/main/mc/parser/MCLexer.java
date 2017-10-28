// Generated from src/main/mc/parser/MC.g4 by ANTLR 4.6

    package mc.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MCLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BOOLEANLIT=1, INTLIT=2, FLOAT=3, STRING=4, INTTYPE=5, VOIDTYPE=6, BOOLEANTYPE=7, 
		FLOATTYPE=8, STRINGTYPE=9, BREAK=10, CONTINUE=11, ELSE=12, IF=13, RETURN=14, 
		WHILE=15, DO=16, FOR=17, TRUE=18, FALSE=19, SUB=20, ADD=21, MUL=22, NOT=23, 
		OR=24, NOTEQUAL=25, LESS=26, LESSEQUAL=27, ASSIGN=28, DIV=29, MOD=30, 
		AND=31, EQUAL=32, GREATER=33, GREATEREQUAL=34, LSB=35, RSB=36, LP=37, 
		RP=38, LB=39, RB=40, SM=41, CM=42, ID=43, WS=44, COMMENT_LINE=45, COMMENT_BLOCK=46, 
		ILLEGAL_ESCAPE=47, UNCLOSE_STRING=48, ERROR_CHAR=49;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"BOOLEANLIT", "INTLIT", "FLOAT", "Digits", "ExponentPart", "STRING", "Escape", 
		"SingleCharacter", "StringCharacters", "INTTYPE", "VOIDTYPE", "BOOLEANTYPE", 
		"FLOATTYPE", "STRINGTYPE", "BREAK", "CONTINUE", "ELSE", "IF", "RETURN", 
		"WHILE", "DO", "FOR", "TRUE", "FALSE", "SUB", "ADD", "MUL", "NOT", "OR", 
		"NOTEQUAL", "LESS", "LESSEQUAL", "ASSIGN", "DIV", "MOD", "AND", "EQUAL", 
		"GREATER", "GREATEREQUAL", "LSB", "RSB", "LP", "RP", "LB", "RB", "SM", 
		"CM", "ID", "WS", "COMMENT_LINE", "COMMENT_BLOCK", "ILLEGAL_ESCAPE", "UNCLOSE_STRING", 
		"ERROR_CHAR"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, null, "'int'", "'void'", "'boolean'", "'float'", 
		"'string'", "'break'", "'continue'", "'else'", "'if'", "'return'", "'while'", 
		"'do'", "'for'", "'true'", "'false'", "'-'", "'+'", "'*'", "'!'", "'||'", 
		"'!='", "'<'", "'<='", "'='", "'/'", "'%'", "'&&'", "'=='", "'>'", "'>='", 
		"'['", "']'", "'{'", "'}'", "'('", "')'", "';'", "','"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "BOOLEANLIT", "INTLIT", "FLOAT", "STRING", "INTTYPE", "VOIDTYPE", 
		"BOOLEANTYPE", "FLOATTYPE", "STRINGTYPE", "BREAK", "CONTINUE", "ELSE", 
		"IF", "RETURN", "WHILE", "DO", "FOR", "TRUE", "FALSE", "SUB", "ADD", "MUL", 
		"NOT", "OR", "NOTEQUAL", "LESS", "LESSEQUAL", "ASSIGN", "DIV", "MOD", 
		"AND", "EQUAL", "GREATER", "GREATEREQUAL", "LSB", "RSB", "LP", "RP", "LB", 
		"RB", "SM", "CM", "ID", "WS", "COMMENT_LINE", "COMMENT_BLOCK", "ILLEGAL_ESCAPE", 
		"UNCLOSE_STRING", "ERROR_CHAR"
	};
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
	public Token emit() {
	    switch (getType()) {
	    case UNCLOSE_STRING:       
	        Token result = super.emit();
	        // you'll need to define this method
	        throw new UncloseString(result.getText());
	        
	    case ILLEGAL_ESCAPE:
	        result = super.emit();
	        throw new IllegalEscape(result.getText());

	    case ERROR_CHAR:
	        result = super.emit();
	        throw new ErrorToken(result.getText()); 

	    default:
	        return super.emit();
	    }
	}


	public MCLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MC.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 5:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 51:
			ILLEGAL_ESCAPE_action((RuleContext)_localctx, actionIndex);
			break;
		case 52:
			UNCLOSE_STRING_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:

			    String str = getText();
			    str = str.substring(1,str.length() -1);
			    setText(str);

			break;
		}
	}
	private void ILLEGAL_ESCAPE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:

			    String str = getText();
			    str = str.substring(1);
			    setText(str);   

			break;
		}
	}
	private void UNCLOSE_STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:

			    String str = getText();
			    str = str.substring(1);
			    setText(str);   

			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\63\u016f\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\5\2r\n\2\3\3\6\3u\n\3\r\3"+
		"\16\3v\3\4\3\4\3\4\5\4|\n\4\3\4\5\4\177\n\4\3\4\3\4\3\4\5\4\u0084\n\4"+
		"\3\4\3\4\3\4\5\4\u0089\n\4\3\5\6\5\u008c\n\5\r\5\16\5\u008d\3\6\3\6\5"+
		"\6\u0092\n\6\3\6\3\6\3\7\3\7\5\7\u0098\n\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t"+
		"\3\t\5\t\u00a2\n\t\3\n\6\n\u00a5\n\n\r\n\16\n\u00a6\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3"+
		"\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3"+
		"\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3"+
		"$\3$\3%\3%\3%\3&\3&\3&\3\'\3\'\3(\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-"+
		"\3.\3.\3/\3/\3\60\3\60\3\61\3\61\7\61\u0133\n\61\f\61\16\61\u0136\13\61"+
		"\3\62\6\62\u0139\n\62\r\62\16\62\u013a\3\62\3\62\3\63\3\63\3\63\3\63\7"+
		"\63\u0143\n\63\f\63\16\63\u0146\13\63\3\63\3\63\3\64\3\64\3\64\3\64\7"+
		"\64\u014e\n\64\f\64\16\64\u0151\13\64\3\64\3\64\3\64\3\64\3\64\3\65\3"+
		"\65\7\65\u015a\n\65\f\65\16\65\u015d\13\65\3\65\3\65\5\65\u0161\n\65\3"+
		"\65\3\65\3\66\3\66\7\66\u0167\n\66\f\66\16\66\u016a\13\66\3\66\3\66\3"+
		"\67\3\67\4\u014f\u015b\28\3\3\5\4\7\5\t\2\13\2\r\6\17\2\21\2\23\2\25\7"+
		"\27\b\31\t\33\n\35\13\37\f!\r#\16%\17\'\20)\21+\22-\23/\24\61\25\63\26"+
		"\65\27\67\309\31;\32=\33?\34A\35C\36E\37G I!K\"M#O$Q%S&U\'W(Y)[*]+_,a"+
		"-c.e/g\60i\61k\62m\63\3\2\13\3\2\62;\4\2GGgg\3\2//\n\2$$))^^ddhhppttv"+
		"v\6\2\f\f\17\17$$^^\5\2C\\aac|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f"+
		"\f\17\17\u017c\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\r\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2"+
		"\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2"+
		"\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q"+
		"\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2"+
		"\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2"+
		"\2k\3\2\2\2\2m\3\2\2\2\3q\3\2\2\2\5t\3\2\2\2\7\u0088\3\2\2\2\t\u008b\3"+
		"\2\2\2\13\u008f\3\2\2\2\r\u0095\3\2\2\2\17\u009c\3\2\2\2\21\u00a1\3\2"+
		"\2\2\23\u00a4\3\2\2\2\25\u00a8\3\2\2\2\27\u00ac\3\2\2\2\31\u00b1\3\2\2"+
		"\2\33\u00b9\3\2\2\2\35\u00bf\3\2\2\2\37\u00c6\3\2\2\2!\u00cc\3\2\2\2#"+
		"\u00d5\3\2\2\2%\u00da\3\2\2\2\'\u00dd\3\2\2\2)\u00e4\3\2\2\2+\u00ea\3"+
		"\2\2\2-\u00ed\3\2\2\2/\u00f1\3\2\2\2\61\u00f6\3\2\2\2\63\u00fc\3\2\2\2"+
		"\65\u00fe\3\2\2\2\67\u0100\3\2\2\29\u0102\3\2\2\2;\u0104\3\2\2\2=\u0107"+
		"\3\2\2\2?\u010a\3\2\2\2A\u010c\3\2\2\2C\u010f\3\2\2\2E\u0111\3\2\2\2G"+
		"\u0113\3\2\2\2I\u0115\3\2\2\2K\u0118\3\2\2\2M\u011b\3\2\2\2O\u011d\3\2"+
		"\2\2Q\u0120\3\2\2\2S\u0122\3\2\2\2U\u0124\3\2\2\2W\u0126\3\2\2\2Y\u0128"+
		"\3\2\2\2[\u012a\3\2\2\2]\u012c\3\2\2\2_\u012e\3\2\2\2a\u0130\3\2\2\2c"+
		"\u0138\3\2\2\2e\u013e\3\2\2\2g\u0149\3\2\2\2i\u0157\3\2\2\2k\u0164\3\2"+
		"\2\2m\u016d\3\2\2\2or\5/\30\2pr\5\61\31\2qo\3\2\2\2qp\3\2\2\2r\4\3\2\2"+
		"\2su\t\2\2\2ts\3\2\2\2uv\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\6\3\2\2\2xy\5\t"+
		"\5\2y{\7\60\2\2z|\5\t\5\2{z\3\2\2\2{|\3\2\2\2|~\3\2\2\2}\177\5\13\6\2"+
		"~}\3\2\2\2~\177\3\2\2\2\177\u0089\3\2\2\2\u0080\u0081\7\60\2\2\u0081\u0083"+
		"\5\t\5\2\u0082\u0084\5\13\6\2\u0083\u0082\3\2\2\2\u0083\u0084\3\2\2\2"+
		"\u0084\u0089\3\2\2\2\u0085\u0086\5\t\5\2\u0086\u0087\5\13\6\2\u0087\u0089"+
		"\3\2\2\2\u0088x\3\2\2\2\u0088\u0080\3\2\2\2\u0088\u0085\3\2\2\2\u0089"+
		"\b\3\2\2\2\u008a\u008c\t\2\2\2\u008b\u008a\3\2\2\2\u008c\u008d\3\2\2\2"+
		"\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\n\3\2\2\2\u008f\u0091\t"+
		"\3\2\2\u0090\u0092\t\4\2\2\u0091\u0090\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\u0093\3\2\2\2\u0093\u0094\5\t\5\2\u0094\f\3\2\2\2\u0095\u0097\7$\2\2"+
		"\u0096\u0098\5\23\n\2\u0097\u0096\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0099"+
		"\3\2\2\2\u0099\u009a\7$\2\2\u009a\u009b\b\7\2\2\u009b\16\3\2\2\2\u009c"+
		"\u009d\7^\2\2\u009d\u009e\t\5\2\2\u009e\20\3\2\2\2\u009f\u00a2\n\6\2\2"+
		"\u00a0\u00a2\5\17\b\2\u00a1\u009f\3\2\2\2\u00a1\u00a0\3\2\2\2\u00a2\22"+
		"\3\2\2\2\u00a3\u00a5\5\21\t\2\u00a4\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2"+
		"\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\24\3\2\2\2\u00a8\u00a9"+
		"\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7v\2\2\u00ab\26\3\2\2\2\u00ac\u00ad"+
		"\7x\2\2\u00ad\u00ae\7q\2\2\u00ae\u00af\7k\2\2\u00af\u00b0\7f\2\2\u00b0"+
		"\30\3\2\2\2\u00b1\u00b2\7d\2\2\u00b2\u00b3\7q\2\2\u00b3\u00b4\7q\2\2\u00b4"+
		"\u00b5\7n\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7c\2\2\u00b7\u00b8\7p\2\2"+
		"\u00b8\32\3\2\2\2\u00b9\u00ba\7h\2\2\u00ba\u00bb\7n\2\2\u00bb\u00bc\7"+
		"q\2\2\u00bc\u00bd\7c\2\2\u00bd\u00be\7v\2\2\u00be\34\3\2\2\2\u00bf\u00c0"+
		"\7u\2\2\u00c0\u00c1\7v\2\2\u00c1\u00c2\7t\2\2\u00c2\u00c3\7k\2\2\u00c3"+
		"\u00c4\7p\2\2\u00c4\u00c5\7i\2\2\u00c5\36\3\2\2\2\u00c6\u00c7\7d\2\2\u00c7"+
		"\u00c8\7t\2\2\u00c8\u00c9\7g\2\2\u00c9\u00ca\7c\2\2\u00ca\u00cb\7m\2\2"+
		"\u00cb \3\2\2\2\u00cc\u00cd\7e\2\2\u00cd\u00ce\7q\2\2\u00ce\u00cf\7p\2"+
		"\2\u00cf\u00d0\7v\2\2\u00d0\u00d1\7k\2\2\u00d1\u00d2\7p\2\2\u00d2\u00d3"+
		"\7w\2\2\u00d3\u00d4\7g\2\2\u00d4\"\3\2\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7"+
		"\7n\2\2\u00d7\u00d8\7u\2\2\u00d8\u00d9\7g\2\2\u00d9$\3\2\2\2\u00da\u00db"+
		"\7k\2\2\u00db\u00dc\7h\2\2\u00dc&\3\2\2\2\u00dd\u00de\7t\2\2\u00de\u00df"+
		"\7g\2\2\u00df\u00e0\7v\2\2\u00e0\u00e1\7w\2\2\u00e1\u00e2\7t\2\2\u00e2"+
		"\u00e3\7p\2\2\u00e3(\3\2\2\2\u00e4\u00e5\7y\2\2\u00e5\u00e6\7j\2\2\u00e6"+
		"\u00e7\7k\2\2\u00e7\u00e8\7n\2\2\u00e8\u00e9\7g\2\2\u00e9*\3\2\2\2\u00ea"+
		"\u00eb\7f\2\2\u00eb\u00ec\7q\2\2\u00ec,\3\2\2\2\u00ed\u00ee\7h\2\2\u00ee"+
		"\u00ef\7q\2\2\u00ef\u00f0\7t\2\2\u00f0.\3\2\2\2\u00f1\u00f2\7v\2\2\u00f2"+
		"\u00f3\7t\2\2\u00f3\u00f4\7w\2\2\u00f4\u00f5\7g\2\2\u00f5\60\3\2\2\2\u00f6"+
		"\u00f7\7h\2\2\u00f7\u00f8\7c\2\2\u00f8\u00f9\7n\2\2\u00f9\u00fa\7u\2\2"+
		"\u00fa\u00fb\7g\2\2\u00fb\62\3\2\2\2\u00fc\u00fd\7/\2\2\u00fd\64\3\2\2"+
		"\2\u00fe\u00ff\7-\2\2\u00ff\66\3\2\2\2\u0100\u0101\7,\2\2\u01018\3\2\2"+
		"\2\u0102\u0103\7#\2\2\u0103:\3\2\2\2\u0104\u0105\7~\2\2\u0105\u0106\7"+
		"~\2\2\u0106<\3\2\2\2\u0107\u0108\7#\2\2\u0108\u0109\7?\2\2\u0109>\3\2"+
		"\2\2\u010a\u010b\7>\2\2\u010b@\3\2\2\2\u010c\u010d\7>\2\2\u010d\u010e"+
		"\7?\2\2\u010eB\3\2\2\2\u010f\u0110\7?\2\2\u0110D\3\2\2\2\u0111\u0112\7"+
		"\61\2\2\u0112F\3\2\2\2\u0113\u0114\7\'\2\2\u0114H\3\2\2\2\u0115\u0116"+
		"\7(\2\2\u0116\u0117\7(\2\2\u0117J\3\2\2\2\u0118\u0119\7?\2\2\u0119\u011a"+
		"\7?\2\2\u011aL\3\2\2\2\u011b\u011c\7@\2\2\u011cN\3\2\2\2\u011d\u011e\7"+
		"@\2\2\u011e\u011f\7?\2\2\u011fP\3\2\2\2\u0120\u0121\7]\2\2\u0121R\3\2"+
		"\2\2\u0122\u0123\7_\2\2\u0123T\3\2\2\2\u0124\u0125\7}\2\2\u0125V\3\2\2"+
		"\2\u0126\u0127\7\177\2\2\u0127X\3\2\2\2\u0128\u0129\7*\2\2\u0129Z\3\2"+
		"\2\2\u012a\u012b\7+\2\2\u012b\\\3\2\2\2\u012c\u012d\7=\2\2\u012d^\3\2"+
		"\2\2\u012e\u012f\7.\2\2\u012f`\3\2\2\2\u0130\u0134\t\7\2\2\u0131\u0133"+
		"\t\b\2\2\u0132\u0131\3\2\2\2\u0133\u0136\3\2\2\2\u0134\u0132\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135b\3\2\2\2\u0136\u0134\3\2\2\2\u0137\u0139\t\t\2\2"+
		"\u0138\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b"+
		"\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013d\b\62\3\2\u013dd\3\2\2\2\u013e"+
		"\u013f\7\61\2\2\u013f\u0140\7\61\2\2\u0140\u0144\3\2\2\2\u0141\u0143\n"+
		"\n\2\2\u0142\u0141\3\2\2\2\u0143\u0146\3\2\2\2\u0144\u0142\3\2\2\2\u0144"+
		"\u0145\3\2\2\2\u0145\u0147\3\2\2\2\u0146\u0144\3\2\2\2\u0147\u0148\b\63"+
		"\3\2\u0148f\3\2\2\2\u0149\u014a\7\61\2\2\u014a\u014b\7,\2\2\u014b\u014f"+
		"\3\2\2\2\u014c\u014e\13\2\2\2\u014d\u014c\3\2\2\2\u014e\u0151\3\2\2\2"+
		"\u014f\u0150\3\2\2\2\u014f\u014d\3\2\2\2\u0150\u0152\3\2\2\2\u0151\u014f"+
		"\3\2\2\2\u0152\u0153\7,\2\2\u0153\u0154\7\61\2\2\u0154\u0155\3\2\2\2\u0155"+
		"\u0156\b\64\3\2\u0156h\3\2\2\2\u0157\u015b\7$\2\2\u0158\u015a\13\2\2\2"+
		"\u0159\u0158\3\2\2\2\u015a\u015d\3\2\2\2\u015b\u015c\3\2\2\2\u015b\u0159"+
		"\3\2\2\2\u015c\u015e\3\2\2\2\u015d\u015b\3\2\2\2\u015e\u0160\7^\2\2\u015f"+
		"\u0161\n\5\2\2\u0160\u015f\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162\3\2"+
		"\2\2\u0162\u0163\b\65\4\2\u0163j\3\2\2\2\u0164\u0168\7$\2\2\u0165\u0167"+
		"\5\21\t\2\u0166\u0165\3\2\2\2\u0167\u016a\3\2\2\2\u0168\u0166\3\2\2\2"+
		"\u0168\u0169\3\2\2\2\u0169\u016b\3\2\2\2\u016a\u0168\3\2\2\2\u016b\u016c"+
		"\b\66\5\2\u016cl\3\2\2\2\u016d\u016e\13\2\2\2\u016en\3\2\2\2\25\2qv{~"+
		"\u0083\u0088\u008d\u0091\u0097\u00a1\u00a6\u0134\u013a\u0144\u014f\u015b"+
		"\u0160\u0168\6\3\7\2\b\2\2\3\65\3\3\66\4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}