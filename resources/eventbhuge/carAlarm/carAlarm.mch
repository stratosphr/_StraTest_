MACHINE
    CarAlarm

VARIABLES
    A, P, S, CA, Wa, Vi, E, M, D, T, F, Ut

INVARIANT
    A : 0..1 &
    P : 0..1 &
    S : 0..1 &
    CA : 0..1 &
    Wa : 0..1 &
    Vi : 0..1 &
    E : 0..1 &
    M : 0..1 &
    D : 0..10 &
    T : 0..1 &
    F : 0..1 &
    Ut : 0..1 &
    ((not (CA = 1 & Ut = 1)) or P = 0) &
    ((not (A = 1)) or (CA = 1)) &
    ((not (CA = 0)) or (A = 0)) &
    ((not (Wa = 1)) or (A = 1)) &
    ((not (A = 1)) or (Wa = 1)) &
    ((not (Wa = 0)) or (A = 0)) &
    ((not (S = 1)) or ((P = 0) or (Ut = 0))) &
    ((not (A = 0)) or (Wa = 0)) &
    ((not (Ut = 1)) or (A = 0)) &
    ((not (S = 1)) or (Vi = 0)) &
    ((not (A = 1)) or (Vi = 0)) &
    ((not (M = 1)) or (P = 0)) &
    ((not (M = 1)) or (CA = 0)) &
    ((not (M = 1)) or (Ut = 1)) &
    ((not (M = 1)) or (D = 0 & T = 0)) &
    ((not (F = 1)) or (S = 0)) &
    ((not (F = 1)) or (CA = 0)) &
    ((not (F = 1)) or (T = 0)) &
    ((not (T = 1)) or (S = 1)) &
    ((not (S = 0)) or (S = 0))

INITIALISATION
    P := 0 ||
    S := 0 ||
    CA := 0 ||
    Ut := 1 ||
    A := 0 ||
    F := 0 ||
    Wa := 0 ||
    Vi := 0 ||
    E := 0 ||
    M := 0 ||
    D := 0 ||
    T := 0

OPERATIONS

    e1 =
        SELECT
            T = 0 & M = 0 & P = 0 & (Ut = 0 or (Ut = 1 & S = 0 & CA = 0))
        THEN
            P := 1
        END;

    e2 =
        SELECT
            T = 0 & M = 0 & P = 1 & Ut = 1
        THEN
            P := 0
        END;

    e3 =
        SELECT
            T = 0 & M = 0 & Ut = 1 & A = 1 & Wa = 1 & CA = 1
        THEN
            CA := 0
        END;

    e4 =
        SELECT
            T = 0 & M = 0 & CA = 1 & P = 1
        THEN
            Wa := 1 ||
            A := 1
        END;

    e5 =
        SELECT
            T = 0 & M = 0 & A = 1 & CA = 0
        THEN
            A := 0 ||
            Wa := 0
        END;

    e6 =
        SELECT
            T = 0 & M = 0 & Ut = 0 & A = 1
        THEN
            Ut := 1 ||
            A := 0 ||
            Wa := 0 ||
            CA := 0 ||
            S := 0
        END;

    e7 =
        SELECT
            T = 0 & M = 0 & Ut = 1 & P = 0 & CA = 0 & S = 1
        THEN
            Ut := 0
        END;

    e8 =
        SELECT
            T = 1 & M = 0 & S = 1 & P = 0 & CA = 0 & D : 0..10
        THEN
            CA := 1 ||
            T := 0 ||
            D := 0
        END;

    e9 =
        SELECT
            T = 0 & M = 0 & Ut = 1 & S = 0 & CA = 1 & P = 0
        THEN
            CA := 0
        END;

    e10 =
        SELECT
            T = 0 & S = 0 & P = 0 & Ut = 1 & Vi = 0 & M = 0 & CA = 0
        THEN
            S := 1 ||
            F := 0 ||
            T := 1 ||
            D := 0
        END;

    e11 =
        SELECT
            T = 0 & S = 1 & P = 0 & Ut = 1
        THEN
            S := 0
        END;

    e12 =
        SELECT
            T = 0 & Vi = 0 & S = 0
        THEN
            Vi := 1
        END;

    e13 =
        SELECT
            T = 0 & Vi = 1 & S = 0
        THEN
            Vi := 0
        END;

    e14 =
        SELECT
            T = 0 & M = 0 & S = 0 & E = 0 & P = 1
        THEN
            E := 1
        END;

    e15 =
        SELECT
            T = 0 & M = 0 & S = 0 & E = 1 & P = 1
        THEN
            E := 0
        END;

    e16 =
        SELECT
            T = 0 & M = 0 & P = 0 & S = 0 & CA = 0
        THEN
            M := 1
        END;

    e17 =
        SELECT
            T = 0 & M = 1 & S = 0 & P = 0 & CA = 0
        THEN
            M := 0
        END;

    e18 =
        SELECT
            T = 0 & F = 0 & S = 0 & CA = 0
        THEN
            F := 1
        END;

    e19 =
        SELECT
            T = 0 & F = 1 & S = 0 & CA = 0
        THEN
            F := 0
        END;

    e20 =
        SELECT
            T = 1 & D < 10
        THEN
            D := D + 1
        END

END
