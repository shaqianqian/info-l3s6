module ParserCours where

import Prelude hiding (pure, (>>=))
import Data.Char (isDigit)
import Control.Parallel

-- | Quels types ?

-- type Parser = String -> ArbreAbstraitDeSyntaxe
-- type Parser = String -> (ArbreAbstraitDeSyntaxe, String)
-- type Parser a = String -> (a, String)
-- type Parser a = String -> Maybe (a, String)

type ResultatParser a = Maybe (a, String)
data Parser a = MkParser (String -> ResultatParser a)


-- | Fonctions de base, briques de base

empty :: Parser a
empty = MkParser (const Nothing)

pure :: a -> Parser a
pure v = MkParser f
    where f cs = Just (v, cs)

unCaractèreQuelconque :: Parser Char
unCaractèreQuelconque = MkParser f
    where f     "" = Nothing
          f (c:cs) = Just (c, cs)

runParser :: Parser a -> String -> ResultatParser a
-- runParser (MkParser f) cs = f cs
runParser (MkParser f) = f

resultat :: ResultatParser a -> a
resultat (Just (r, _)) = r

-- | Combinateurs

-- Alternative entre deux parseurs
(<|>) :: Parser a -> Parser a -> Parser a
p1 <|> p2 = MkParser f
    where f cs = case runParser p1 cs of
                    Nothing -> runParser p2 cs
                    r       -> r

-- (>>=) :: Parser a -> Parser b -> Parser (a, b)
-- (>>=) :: (a -> b -> c) -> Parser a -> Parser b -> Parser c
(>>=) :: Parser a -> (a -> Parser b) -> Parser b
p >>= fp = MkParser f
    where f cs = case runParser p cs of
                    Nothing -> Nothing
                    Just (r, cs') -> runParser (fp r) cs'

-- | Utilisation

carQuand :: (Char -> Bool) -> Parser Char
carQuand cond = unCaractèreQuelconque >>= filtre
    where filtre c | cond c    = pure c
                   | otherwise = empty

ex1 = carQuand isDigit
ex2 = runParser ex1 ""
ex3 = runParser ex1 "abc"
ex4 = runParser ex1 "123"
ex5 = runParser ex1 "1abc"

-- | Parser qui réussit si l’entrée commence par le
-- caractère indiqué
car :: Char -> Parser Char
car c = carQuand (c ==)

ex6 = runParser (car 'a') "abc"
ex7 = runParser (car 'a') "123"
ex8 = runParser (car 'a') ""

-- | Parser qui réussit si l’entrée commence par la chaîne
-- indiquée
chaine :: String -> Parser String
chaine     "" = pure ""
chaine (c:cs) = car c     >>= \_ ->
                chaine cs >>= \_ ->
                pure (c:cs)
-- chaine (c:cs) = car c     >>= (\_ ->
--                     chaine cs >>= (\_ ->
--                         pure (c:cs) ))

ex9  = runParser (chaine "abc") "abcdef"
ex10 = runParser (chaine "abc") "abdef"
ex11 = runParser (chaine "abc") "ab"
ex12 = runParser (chaine "abc" <|> pure "test") "abdef"

chiffre :: Parser Char
chiffre = carQuand isDigit

suiteDeChiffres :: Parser String
suiteDeChiffres = ( chiffre         >>= \c ->
                    suiteDeChiffres >>= \cs ->
                    pure (c:cs) )
                  <|> pure ""

nombre :: Parser Integer
nombre = suiteDeChiffres >>= \cs -> pure (read cs)

ex13 = runParser suiteDeChiffres "123abc"
ex14 = runParser suiteDeChiffres "1a2b3c"
ex15 = runParser suiteDeChiffres "a2b3c"
ex16 = runParser nombre "123abc"
ex17 = runParser nombre "1a2b3c"
ex18 = runParser nombre "a2b3c"

-- zéro ou plus
many :: Parser a -> Parser [a]
many p = some p <|> pure []

-- un ou plus
some :: Parser a -> Parser [a]
some p = p      >>= \r ->
         many p >>= \rs ->
         pure (r:rs)

nombre' :: Parser Integer
nombre' = some chiffre >>= \cs -> pure (read cs)

ex19 = runParser nombre' "123abc"
ex20 = runParser nombre' "1a2b3c"
ex21 = runParser nombre' "a2b3c"


-- | Analyseur d’expressions

-- Nombre  ::= Chiffres+
-- Facteur ::= Nombre | '(' Expr ')'
-- Terme   ::= Facteur '*' Terme | Facteur
-- Expr    ::= Terme '+' Expr | Terme

data Expression a = Nombre a
                  | Add (Expression a) (Expression a)
                  | Mult (Expression a) (Expression a)
    deriving (Show, Eq, Read)

type ParserExpr = Parser (Expression Integer)

nombreExp :: ParserExpr
-- nombreExp = some chiffre >>= \cs -> pure (Nombre (read cs))
nombreExp = some chiffre >>= (pure . Nombre . read)

facteurExp :: ParserExpr
facteurExp = nombreExp
         <|> ( car '('       >>= \_   ->
               expressionExp >>= \exp ->
               car ')'       >>= \_   ->
               pure exp )

termeExp :: ParserExpr
termeExp = ( facteurExp >>= \f ->
             car '*'    >>= \_ ->
             termeExp   >>= \t ->
             pure (Mult f t) )
           <|> facteurExp

expressionExp :: ParserExpr
expressionExp = ( termeExp      >>= \t ->
                  car '+'       >>= \_ ->
                  expressionExp >>= \e ->
                  pure (Add t e) )
                <|> termeExp

ex22 = runParser expressionExp "1+2*3+4"
ex23 = runParser expressionExp "(1+2)*3+4"

evalExpr :: Expression Integer -> Integer
evalExpr (Nombre n) = n
evalExpr (Add e e') = evalExpr e + evalExpr e'
evalExpr (Mult e e') = evalExpr e * evalExpr e'

evalExpr' :: Expression Integer -> Integer
evalExpr' (Nombre n) = n
evalExpr' (Add e e') = eEval `par` (e'Eval `pseq` eEval + e'Eval)
    where eEval = evalExpr' e
          e'Eval = evalExpr' e'
evalExpr' (Mult e e') = eEval `par` (e'Eval `pseq` eEval * e'Eval)
    where eEval = evalExpr' e
          e'Eval = evalExpr' e'

ex24 = evalExpr $ resultat $ runParser expressionExp "1+2*3+4"
ex25 = evalExpr $ resultat $ runParser expressionExp "(1+2)*3+4"

parseEval :: String -> Integer
parseEval = evalExpr . resultat . runParser expressionExp
