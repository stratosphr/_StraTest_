MACHINE
	Fragment

VARIABLES
	pc, x

INVARIANT
	x : NAT & pc : 0..3

INITIALISATION
	pc := 0 || ANY k WHERE k : NAT THEN x := k END

OPERATIONS
	e1 = SELECT pc=0 & x < 3 THEN pc := 1 || x := x + 1 END;
	e2 = SELECT pc=0 & x >= 3 THEN pc := 1 || x := x - 1 END;
	e3 = SELECT pc=1 & x < 3 THEN pc := 2 || x := x + 1 END;
	e4 = SELECT pc=1 & x >= 3 THEN pc := 2 || x := x - 1 END;
	e5 = SELECT pc=2 THEN pc := 3 END
END
