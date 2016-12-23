MACHINE 
	RobotTwoEvacuations

SETS	
	Cote = {1, 2};
	Position = {3, 4};
	EtatD = {5, 6, 7, 8}

VARIABLES	
	Deg, Dt, Ded, CoteDt, PosDt, Da

INVARIANT
	Deg : EtatD-{7} & Ded : EtatD-{6} & Dt : EtatD 
	&
	CoteDt : Cote & PosDt : Position & Da : EtatD 
	&
	((CoteDt=2 & Dt /= 5) => (Dt=7 or (Dt=8 & Ded=5) or Dt = 1)) 
	&
	(PosDt=4 => CoteDt=1) 

INITIALISATION	
	Deg, Dt, Ded, Da := 5, 5, 5, 5
	|| CoteDt := 1 
	|| PosDt := 4

OPERATIONS
	A_P = 
		SELECT 
			Da=5
		THEN
			ANY p WHERE p : EtatD-{5} THEN Da := p END
		END;

	Chargt = 
		SELECT 
			Dt=5 & Da/=5 & CoteDt=1 & PosDt=4 
		THEN 
			Dt := Da || Da := 5
		END;

	EvacG =
		SELECT 
			Deg/=5 
		THEN 
			Deg := 5 
		END;
	
	EvacD = 
		SELECT 
			Ded/=5 
		THEN 
			Ded := 5 
		END;

	Montee =
		SELECT 
			PosDt=4 & Dt/=5 
		THEN 
			PosDt := 3 
		END;

	Descente =
		SELECT 
			PosDt=3 & Dt=5 & CoteDt=1 
		THEN 
			PosDt := 4 
		END;

	RotationD =
		SELECT 
			PosDt=3 & CoteDt=1 & (Dt=7 or (Dt=8 & Ded=5 & Deg/=5))
		THEN 
			CoteDt := 2 
		END;

	RotationG =
		SELECT 
			PosDt=3 & CoteDt=2 & (Dt=5 or (Dt=8 & Deg=5))
		THEN 
			CoteDt := 1 
		END;

	Dechargt =
		SELECT 
			PosDt=3 & ((Deg=5 & CoteDt=1 & Dt : {6, 8}) or (Ded=5 & CoteDt=2 & (Dt=7 or (Dt=8 & Deg/=5))))
		THEN
			IF 
				Deg=5 & CoteDt=1 
			THEN 
				Deg := Dt
			ELSE IF Deg = 5 THEN
				Ded := Dt 
				END
			END
			|| 
			Dt := 5
		END
END


