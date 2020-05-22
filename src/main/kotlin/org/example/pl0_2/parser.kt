package org.example

import org.example.pl0.*
import org.example.topDownParser.block


private enum class Precedence {
    LOWEST,
    EQUALS,
    LESSGREATER,
    SUM,
    PRODUCT,
    PREFIX,
    CALL
}

private val precedenceMap = mapOf<TokenKind, Precedence>(
    TokenKind.EQUAL to Precedence.EQUALS,
    TokenKind.NOTEQ to Precedence.EQUALS,
    TokenKind.LSS to Precedence.LESSGREATER,
    TokenKind.GRT to Precedence.LESSGREATER,
    TokenKind.GRTEQ to Precedence.LESSGREATER,
    TokenKind.LSSEQ to Precedence.LESSGREATER,
    TokenKind.PLUS to Precedence.SUM,
    TokenKind.MINUS to Precedence.SUM,
    TokenKind.DIV to Precedence.PRODUCT,
    TokenKind.MULTI to Precedence.PRODUCT

)
/*
* p.registerPrefix(token.IDENT, p.parseIdentifier)
	p.registerPrefix(token.INT, p.parseIntegerLiteral)
	p.registerPrefix(token.BANG, p.parsePrefixExpression)
	p.registerPrefix(token.MINUS, p.parsePrefixExpression)
	p.registerPrefix(token.TRUE, p.parseBoolean)
	p.registerPrefix(token.FALSE, p.parseBoolean)
	p.registerPrefix(token.LPAREN, p.parseGroupedExpression)
	p.registerPrefix(token.IF, p.parseIfExpression)
	p.registerPrefix(token.FUNCTION, p.parseFunctionLiteral)

	p.infixParseFns = make(map[token.TokenType]infixParseFn)
	p.registerInfix(token.PLUS, p.parseInfixExpression)
	p.registerInfix(token.MINUS, p.parseInfixExpression)
	p.registerInfix(token.SLASH, p.parseInfixExpression)
	p.registerInfix(token.ASTERISK, p.parseInfixExpression)
	p.registerInfix(token.EQ, p.parseInfixExpression)
	p.registerInfix(token.NOT_EQ, p.parseInfixExpression)
	p.registerInfix(token.LT, p.parseInfixExpression)
	p.registerInfix(token.GT, p.parseInfixExpression)

	p.registerInfix(token.LPAREN, p.parseCallExpression)

* */

class Parser(input: String) {

    private val tokenizer = Lexer(input)

    private var currentToken: Token = tokenizer.nextToken()

    private fun nextToken(): Token {
        val next = tokenizer.nextToken()
        currentToken = next
        return next
    }

    private fun parse() {

    }

    private fun parseProgram() {
        when (currentToken) {
            is VarToken -> {
                parseVarDecl()
            }
            is FuncToken -> {
                parseFuncDecl()
            }
            is ConstToken -> {
                parseConstDecl()
            }
            else -> {
                parseStatement()
            }
        }
    }

    private fun parseConstDecl() {
        /* const ident = number{, ident = number} */
        assertAndReadToken<ConstToken>()
        while (true) {
            parseIdentifier()
            assertAndReadToken<EqualToken>()
            val number = assertAndReadToken<IntToken>()
            if (currentToken !is CommaToken) {
                break
            }
            assertAndReadToken<CommaToken>()
        }
        assertAndReadToken<SemicolonToken>()
    }

    private fun parseIdentifier(): IdentifierToken {
        return assertAndReadToken()
    }

    private fun parseVarDecl() {
        /*  var ident{, ident} ; */
        assertAndReadToken<VarToken>()
        while (true) {
            val identifier = assertAndReadToken<IdentifierToken>()
            if (currentToken !is CommaToken) {
                break
            }
            assertAndReadToken<CommaToken>()
        }
        assertAndReadToken<SemicolonToken>()
    }

    private fun parseFuncDecl() {
        /* func. identifier ([ident{, ident}]) block; */
        assertAndReadToken<FuncToken>()
        val identifierToken = assertAndReadToken<IdentifierToken>()
        assertAndReadToken<LParenToken>()
        if (currentToken is IdentifierToken) {
            while (true) {
                val arg = assertAndReadToken<IdentifierToken>()
                if (currentToken !is CommaToken) {
                    break
                }
                assertAndReadToken<CommaToken>()
            }
        }
        assertAndReadToken<RParentToken>()
        block()
        assertAndReadToken<SemicolonToken>()
    }

    private fun parseStatement() {
        when (currentToken) {
            is IdentifierToken -> {
                /* ident := expression */
                val token = assertAndReadToken<IdentifierToken>()
                assertNextToken<AssignToken>()
                parseExpression()
            }
            is BeginToken -> {
                /* begin statement{; statement} end */
                assertAndReadToken<BeginToken>()
                while (true) {
                    parseStatement()
                    if (currentToken !is SemicolonToken) {
                        break
                    }
                    assertAndReadToken<SemicolonToken>()
                }
                assertAndReadToken<EndToken>()
            }
            is IfToken -> {
                /* if condition then statement */
                assertAndReadToken<IfToken>()
                parseCondition()
                assertAndReadToken<ThenToken>()
                parseStatement()
            }
            is WhileToken -> {
                /* while condition do statement */
                assertAndReadToken<WhileToken>()
                parseCondition()
                assertAndReadToken<DoToken>()
                parseStatement()
            }
            is ReturnToken -> {
                assertAndReadToken<ReturnToken>()
                parseExpression()
            }
            is WriteToken -> {
                assertAndReadToken<WriteToken>()
                parseExpression()
            }
            is WritelnToken -> {
                assertAndReadToken<WritelnToken>()
            }
        }
    }


    private fun parseCondition() {
        /* expression [=|<>|<|>|<=|>=] expression */
        parseExpression()
        when (currentToken) {
            is EqualToken -> {
                assertAndReadToken<EqualToken>()
            }
            is NotEqToken -> {
                assertAndReadToken<NotEqToken>()
            }
            is LssToken -> {
                assertAndReadToken<LssToken>()
            }
            is LssEqToken -> {
                assertAndReadToken<LssEqToken>()
            }
            is GrtToken -> {
                assertAndReadToken<GrtToken>()
            }
            is GrtEqToken -> {
                assertAndReadToken<GrtEqToken>()
            }
            else -> {
                error("should be comp op")
            }
        }
        nextToken()
        parseExpression()
    }

    private fun parseExpression() {
        /* [+|-]? {term // [+|-] term} */
        if (currentToken is PlusToken) {
            val prefixToken = currentToken
            nextToken()
        }
        if (currentToken is MinusToken) {
            val prefixToken = currentToken
            nextToken()
        }
        while (true) {
            parseTerm()
            if (currentToken !is PlusToken) {
                break
            }
            if (currentToken !is MinusToken) {
                break
            }
            val opToken = nextToken()
        }
    }

    private fun parseTerm() {
        /* factor {*\/ factor} */
        while (true) {
            parseFactor()
            if (currentToken !is MultiToken) {
                break
            }
            if (currentToken !is DivToken) {
                break
            }
            nextToken()
        }
    }

    private fun parseFactor() {
        when (currentToken) {
            is IdentifierToken -> {
                parseIdentifier()
            }
            is IntToken -> {
                parseNumber()
            }
            is IdentifierToken -> {
                assertAndReadToken<LParenToken>()
                /* {express//,} */
                if (currentToken is RParentToken) {
                    nextToken()
                } else {
                    while (true) {
                        parseExpression()
                        if (currentToken !is CommaToken) {
                            break
                        }
                        nextToken()
                    }
                }
            }
            is LParenToken -> {
                assertAndReadToken<LParenToken>()
                expression()
                assertAndReadToken<RParentToken>()
            }
        }
    }

    private fun parseNumber() {
        assertAndReadToken<IntToken>()
    }

    private inline fun <reified T> assertAndReadToken(): T {
        val token = assertTokenIs<T>(currentToken)
        nextToken()
        return token
    }

    private inline fun <reified T> assertCurrentToken(): T {
        return assertTokenIs(currentToken)
    }


    private inline fun <reified T> assertNextToken(): T {
        return assertTokenIs(nextToken())
    }

    private inline fun <reified T> assertTokenIs(token: Token): T {
        if (token is T) {
            return token
        } else {
            error("not expected token ${token.kind} ${token.literal}")
        }
    }
}