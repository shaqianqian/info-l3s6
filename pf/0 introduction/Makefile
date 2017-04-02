TOUS := Somme.class

tout: ${TOUS}

%.class: %.java
	javac -Xlint:all $^

clean:
	rm -f ${TOUS} *.o *.hi

.PHONY: tout clean
