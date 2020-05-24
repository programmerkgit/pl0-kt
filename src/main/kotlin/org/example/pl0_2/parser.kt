package org.example.pl0_2

import org.example.*


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

/**
 * program := block .
 * block := [constDecl|varDecl|funcDecl] statement
 * constDecl := const identifier = number{, identifier = number } ;
 * varDecl := var identifier{, identifier};
 * funcDecl := function ident ([ident{, ident}]) {block};
 * statement := identifier = expression
 *              | begin statement{; statement} end
 *              | if ( condition ) then { statement }
 *              | while (condition) do { statement }
 *              | return expression
 *              | write expression
 *              | writeln
 *              |
 * condition := expression =|<>|<|>|<=|>= expression
 * expression := [+|-] term {+ term}
 * term := factor {(*|/) factor }
 * factor := ident
 *          | number
 *          | ident ([expression{, expression}])
 *          | ( expression )
 *
 * */

class Parser(lexer: Lexer) {

    /*
      index[0] = 0
      lAddr[0] = 3
      index[1] = 2
      lAddr[2] = 4
      localAddr = 5 ?
    * 0 f
    * 1 x
    * 2 y
    * 3 g
    * 4 a
    * 5 b
    * */
    private val levelIndex = mutableMapOf(-1 to 0)
    private val levelAddr = mutableMapOf(-1 to 0)
    private var level: Int = 0
    private var localAddr: Int = 2
    private var curLevelIndex: Int = 0

    private val tokenizer = lexer
    private val nameTable = mutableListOf<TableEntry>()
    val codes = mutableListOf<Instruction>()

    private var currentToken: Token = tokenizer.nextToken()

    private fun nextToken(): Token {
        val next = tokenizer.nextToken()
        currentToken = next
        return next
    }

    fun parse() {
        parseBlock()
    }

    private fun blockBegin(firstAddr: Int = 2) {
        /* level => Table Index */
        levelIndex[level] = nameTable.size
        levelAddr[level] = localAddr
        localAddr = firstAddr
        level++
        return
    }

    /* OK */
    private fun parseBlock(funcEntry: FuncEntry? = null) {
        /* 関数の実行部にjmpする */
        val jmp = Jmp()
        codes.add(jmp)
        loop@ while (currentToken is VarToken || currentToken is FuncToken || currentToken is ConstToken) {
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
                    break@loop
                }
            }
        }
        /* 関数の実行部を確定 */
        jmp.value = codes.size
        /* Tableの関数も修正。なくても動く。最適化のため */
        if (funcEntry != null) {
            funcEntry.rAddr = codes.size
        }
        /*　ブロックの始まり　*/
        codes.add(Ict(localAddr))
        parseStatement()
        if (codes.last() !is Ret) {
            codes.add(Ret(level, funcEntry?.parCount ?: 0))
        }
        /* block end */
        blockEnd()
    }

    private fun blockEnd() {
        level--
        var i = checkNotNull(levelIndex[level])
        (i until nameTable.size).forEach {
            nameTable.removeAt(nameTable.size - 1)
        }
        localAddr = checkNotNull(levelAddr[level])
    }

    /* OK */
    private fun parseConstDecl() {
        /* const ident = number{, ident = number} */
        assertAndReadToken<ConstToken>()
        while (true) {
            val id = parseIdentifier()
            assertAndReadToken<AssignToken>()
            val number = assertAndReadToken<IntToken>()
            addEntry(ConstEntry(id.literal, number.literal.toInt()))
            if (currentToken !is CommaToken) {
                break
            }
            assertAndReadToken<CommaToken>()
        }
        assertAndReadToken<SemicolonToken>()
    }

    /* OK */
    private fun parseIdentifier(): IdentifierToken {
        return assertAndReadToken()
    }

    private fun addEntry(tableEntry: TableEntry) {
        nameTable.add(tableEntry)
        if (tableEntry is VarEntry) {
            localAddr++;
        }
    }

    /* OK */
    private fun parseVarDecl() {
        /*  var ident{, ident} ; */
        assertAndReadToken<VarToken>()
        while (true) {
            val identifier = assertAndReadToken<IdentifierToken>()
            /* add varDecl */
            val entry = VarEntry(identifier.literal, level, localAddr)
            addEntry(entry)
            if (currentToken !is CommaToken) {
                break
            }
            assertAndReadToken<CommaToken>()
        }
        assertAndReadToken<SemicolonToken>()
    }

    /* OK */
    private fun parseFuncDecl() {
        /* function ident ([ident{, ident}]) {block} */
        assertAndReadToken<FuncToken>()
        val identifierToken = assertAndReadToken<IdentifierToken>()
        val funcEntry = FuncEntry(identifierToken.literal, level, codes.size, 0)
        addEntry(funcEntry)
        val index = nameTable.size
        assertAndReadToken<LParenToken>()
        blockBegin()
        /* )ジャない場合 */
        if (currentToken is IdentifierToken) {
            while (true) {
                val parToken = assertAndReadToken<IdentifierToken>()
                addEntry(ParEntry(parToken.literal, level))
                funcEntry.parCount += 1
                if (currentToken !is CommaToken) {
                    break
                }
                assertAndReadToken<CommaToken>()
            }
        }
        assertAndReadToken<RParentToken>()
        var i = 0;
        while (i < funcEntry.parCount) {
            (nameTable[index + i] as ParEntry).parAddr = i - funcEntry.parCount
            i++
        }
        (index until index + funcEntry.parCount).forEach { i ->
        }
        assertAndReadToken<LBraceToken>()
        parseBlock(funcEntry)
        assertAndReadToken<RBraceToken>()
    }

    private fun endPar() {

    }

    private fun parseStatement() {
        when (currentToken) {
            is IdentifierToken -> {
                /* OK */
                /* ident := expression */
                val token = assertAndReadToken<IdentifierToken>()
                when (val entry = findEntry(token.literal)) {
                    is VarEntry -> {
                        addEntry(entry)
                        assertAndReadToken<AssignToken>()
                        parseExpression()
                        codes.add(Sto(entry.level, entry.addr))
                    }
                    is ParEntry -> {
                        addEntry(entry)
                        assertAndReadToken<AssignToken>()
                        parseExpression()
                        codes.add(Sto(entry.level, entry.parAddr))
                    }
                    else -> {
                        error("unepected entry $entry")
                    }
                }
            }
            /* OK */
            is IfToken -> {
                /* if ( condition ) then { statement } */
                assertAndReadToken<IfToken>()
                assertAndReadToken<LParenToken>()
                parseCondition()
                assertAndReadToken<RParentToken>()
                assertAndReadToken<LBraceToken>()
                val jpc = Jpc()
                codes.add(jpc)
                parseStatement()
                /* back patch */
                jpc.value = codes.size
                assertAndReadToken<RBraceToken>()
            }
            /* OK */
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
            /* OK */
            is WhileToken -> {
                /* while ( condition ) do { statement }*/
                assertAndReadToken<WhileToken>()
                assertAndReadToken<LParenToken>()
                val i = codes.size
                parseCondition()
                assertAndReadToken<RParentToken>()
                val jpc = Jpc()
                codes.add(jpc)
                assertAndReadToken<DoToken>()
                assertAndReadToken<LBraceToken>()
                parseStatement()
                codes.add(Jmp(i))
                jpc.value = codes.size
                assertAndReadToken<RBraceToken>()
            }
            /* OK */
            is ReturnToken -> {
                assertAndReadToken<ReturnToken>()
                parseExpression()
                val funcEntry = nameTable[checkNotNull(levelIndex[level - 1]) - 1]
                if (funcEntry !is FuncEntry) {
                    error("entry should be function")
                }
                codes.add(Ret(level, funcEntry.parCount))
            }
            is WriteToken -> {
                /* OK */
                assertAndReadToken<WriteToken>()
                parseExpression()
                codes.add(Wrt())
            }
            is WritelnToken -> {
                assertAndReadToken<WritelnToken>()
                /* OK */
                codes.add(Wrl())
            }
            else -> {

            }
        }
    }

    /* OK* */
    private fun parseCondition() {
        /* expression [=|<>|<|>|<=|>=] expression */
        parseExpression()
        val token = currentToken
        when (token) {
            is EqualToken -> assertAndReadToken<EqualToken>()
            is NotEqToken -> assertAndReadToken<NotEqToken>()
            is LssToken -> assertAndReadToken<LssToken>()
            is LssEqToken -> assertAndReadToken<LssEqToken>()
            is GrtToken -> assertAndReadToken<GrtToken>()
            is GrtEqToken -> assertAndReadToken<GrtEqToken>()
            else -> error("should be comp op")
        }
        parseExpression()
        when (token) {
            is EqualToken -> codes.add(Eq())
            is NotEqToken -> codes.add(NotEq())
            is LssToken -> codes.add(Lss())
            is LssEqToken -> codes.add(LssEq())
            is GrtToken -> codes.add(Grt())
            is GrtEqToken -> codes.add(GrtEq())
            else -> error("should be comp op")
        }
    }

    /* OK */
    private fun parseExpression() {
        /* [+|-]? {term // [+|-] term} */
        when (currentToken) {
            is PlusToken -> {
                nextToken()
                parseTerm()
                codes.add(Add())
            }
            is MinusToken -> {
                nextToken()
                parseTerm()
                codes.add(Sub())
            }
            else -> {
                parseTerm()
            }
        }
        while (currentToken is PlusToken || currentToken is MinusToken) {
            val token = currentToken
            nextToken()
            parseTerm()
            when (token) {
                is PlusToken -> codes.add(Add())
                is MinusToken -> codes.add(Sub())
                else -> error("unexpected path")
            }
        }
    }

    /* OK */
    private fun parseTerm() {
        /* factor {*\/ factor} */
        parseFactor()
        while (currentToken is MultiToken || currentToken is DivToken) {
            val token = currentToken
            nextToken()
            parseFactor()
            when (token) {
                is MultiToken -> codes.add(Mul())
                is DivToken -> codes.add(Div())
                else -> error("unexpected path here")
            }
        }
    }

    private fun findEntry(name: String): TableEntry {
        return checkNotNull(nameTable.findLast { it.name == name })
    }

    /* OK? */
    private fun parseFactor() {
        when (currentToken) {
            is IdentifierToken -> {
                /* semantic */
                when (val entry = findEntry(parseIdentifier().literal)) {
                    /* OK: */
                    is ConstEntry -> {
                        codes.add(Lit(entry.value))
                    }
                    /* OK: */
                    is VarEntry -> {
                        codes.add(Lod(entry.level, entry.addr))
                    }
                    /* OK: */
                    is ParEntry -> {
                        codes.add(Lod(entry.level, entry.parAddr))
                    }
                    is FuncEntry -> {
                        /* f({a //, }?) */
                        var parCount = 0;
                        assertAndReadToken<LParenToken>()
                        /* {a //, } */
                        if (currentToken !is RParentToken) {
                            while (true) {
                                parseExpression()
                                parCount++
                                if (currentToken !is CommaToken) {
                                    break
                                }
                                assertAndReadToken<CommaToken>()
                            }
                        }
                        if (entry.parCount != parCount) {
                            error("count arguments not match")
                        }
                        assertAndReadToken<RParentToken>()
                        /* gen code T Call */
                        codes.add(Cal(entry.level, entry.rAddr))
                    }
                }
            }
            is IntToken -> {
                /* OK */
                val intToken = assertAndReadToken<IntToken>()
                /* semantic */
                codes.add(Lit(intToken.literal.toInt()))
            }
            is LParenToken -> {
                assertAndReadToken<LParenToken>()
                parseExpression()
                assertAndReadToken<RParentToken>()
            }
        }
    }

    /* OK */
    private fun parseNumber(): IntToken {
        return assertAndReadToken()
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