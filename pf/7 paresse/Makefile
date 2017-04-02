tout: fibo test-fibo

fibo: Paresse.hs
	ghc -main-is Paresse.mainFibo Paresse.hs -o fibo

test-fibo: fibo
	./fibo 100000 +RTS -s

.PHONY: test-fibo
