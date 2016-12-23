MACHINE
    ThreeBatteries

SETS
    swState = {1, 2, 3};
	batteryState = {0, 1};
	hState = {0, 1}

VARIABLES 
	bat, sw, h

INVARIANT
	bat : swState --> batteryState &
	sw : swState & h : hState &
	bat(sw) = 1 &
	(bat(1) = 1 or bat(2) = 1 or bat(3) = 1)

INITIALISATION
	h := 1 ||
	sw := 1 ||
	bat(1) := 1 ||
	bat(2) := 1 ||
	bat(3) := 1

/* $op* -> ( card(bat|>{1}) <= 1 ) . $op -> ( card(bat|>{1}) > 1 ) . Arrivee_Tic . Commute */
/* $op* -> ( card(bat|>{1}) <= 1 ) . Reparer_Panne */

OPERATIONS

	Tic =
		SELECT 
			h = 1
		THEN
			h := 0
		END;

	Commute =
		SELECT
			((bat(1) = 1 & bat(2) = 1) or
			(bat(1) = 1 & bat(3) = 1) or
			(bat(2) = 1 & bat(3) = 1))
			& h=0
		THEN
			ANY
				ni
			WHERE
				ni : 1..3 & ni /= sw & bat(ni) = 1
			THEN
				sw := ni ||
				h := 1
			END
		END ;

	Fail =
		SELECT
			((bat(1) = 1 & bat(2) = 1) or
			(bat(1) = 1 & bat(3) = 1) or
			(bat(2) = 1 & bat(3) = 1))
		THEN
			ANY
				nb
			WHERE
				nb : 1..3 & bat(nb) = 1
			THEN
				bat(nb) := 0 ||
				IF
					nb = sw
				THEN
					ANY
						ni
					WHERE
						ni : 1..3 & ni /= sw & bat(ni) = 1
					THEN
						sw := ni
					END
				END
		    END
		END ;

	Repair =
		ANY
			nb
		WHERE
			nb : 1..3 & bat(nb) = 0
		THEN
			bat(nb) := 1
		END

END

