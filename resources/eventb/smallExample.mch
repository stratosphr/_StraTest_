MACHINE
    SmallExample

VARIABLES
    pc, x

INVARIANT
    pc : {0, 1, 2, 3}

INITIALISATION
    ANY
        z
    WHERE


OPERATIONS
    event1 =
        SELECT
            var1 = 0
        THEN
            var2 := 1
        END

END