TOUS := Parallelisme Concurrence Philosophes

tout: ${TOUS}

%: %.hs
	# Active les optimisations, on veut le code le plus efficace
	# possible
	ghc -O2 -threaded -rtsopts -eventlog $^
