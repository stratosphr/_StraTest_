MACHINE
    CoffeeMachine

VARIABLES
    Mago, Balance, CoffeeLeft, Statut

INVARIANT
    Mago >= 0 &
    Balance >= 0 &
    CoffeeLeft >= 0 &
    Statut : 0..2

INITIALISATION
    Statut := 0 ||
    Mago := 0 ||
    Balance := 0 ||
    CoffeeLeft := 100

OPERATIONS

    insert100 =
        SELECT
            Statut = 1
        THEN
            Balance := Balance + 100
        END;

    insert200 =
        SELECT
            Statut = 1
        THEN
            Balance := Balance + 200
        END;

    insert10 =
        SELECT
            Statut = 1
        THEN
            Balance := Balance + 10
        END;

    insert20 =
        SELECT
            Statut = 1
        THEN
            Balance := Balance + 20
        END;

    insert50 =
        SELECT
            Statut = 1
        THEN
            Balance := Balance + 50
        END;

    powerUp =
        SELECT
            Statut = 0
        THEN
            Statut := 1
        END;

    powerDown =
        SELECT
            (not (Statut = 0))
        THEN
            Statut := 0
        END;

    autoOut =
        SELECT
            (CoffeeLeft = 0 or Mago = 6120) & Statut = 1
        THEN
            Statut := 2 ||
            Balance := 0
        END;

    takeMago =
        SELECT
            Statut = 0
        THEN
            Mago := 0
        END;

    addCoffee =
        ANY
            x
        WHERE
            x > 0
        THEN
            SELECT
                Statut = 0
            THEN
                CoffeeLeft := CoffeeLeft + x
            END
        END;

    serveCoffee =
        SELECT
            Statut = 1 & (Balance > 60 or Balance = 60)
        THEN
            CoffeeLeft := CoffeeLeft - 1 ||
            Mago := Mago + 60
        END;

    backBalance =
        SELECT
            Statut = 1 & Balance > 0
        THEN
            Balance := 0
        END

END
