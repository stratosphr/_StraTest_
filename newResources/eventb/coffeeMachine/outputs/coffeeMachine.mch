MACHINE
	coffeeMachine

VARIABLES
	Pot,
	Balance,
	CoffeeLeft,
	Status,
	AskCoffee,
	AskChange,
	MAX_POT,
	MAX_BAL,
	MAX_COFFEE

INVARIANT
	((¬((Balance = 0)) ∨ ((AskCoffee = 0) ∧ (AskCoffee = 0))) ∧ (Status > -1) ∧ (3 > Status) ∧ (MAX_POT = 200) ∧ (MAX_BAL = 200) ∧ (MAX_COFFEE = 20) ∧ ((Pot > 100) ∨ (Pot = 100)) ∧ (((MAX_POT + 50) > Pot) ∨ ((MAX_POT + 50) = Pot)) ∧ (Balance > -1) ∧ ((MAX_BAL > Balance) ∨ (MAX_BAL = Balance)) ∧ (CoffeeLeft > -1) ∧ ((MAX_COFFEE > CoffeeLeft) ∨ (MAX_COFFEE = CoffeeLeft)) ∧ ((AskCoffee = 0) ∨ (AskCoffee = 1)) ∧ ((AskChange = 0) ∨ (AskChange = 1)) ∧ ((Pot = 0) ∨ (Pot = 50) ∨ (Pot = 100) ∨ (Pot = 150) ∨ (Pot = 200)) ∧ ((Balance = 0) ∨ (Balance = 50) ∨ (Balance = 100) ∨ (Balance = 150) ∨ (Balance = 200)) ∧ (¬((AskChange = 1)) ∨ ((AskCoffee = 0) ∧ (Balance > 0))) ∧ (¬((AskCoffee = 1)) ∨ ((AskChange = 0) ∧ ((Balance > 50) ∨ (Balance = 50)))))

INITIALISATION
	Status := 0 ||
	Pot := 100 ||
	Balance := 0 ||
	CoffeeLeft := 10 ||
	AskCoffee := 0 ||
	AskChange := 0 ||
	MAX_POT := 200 ||
	MAX_BAL := 200 ||
	MAX_COFFEE := 20

EVENTS
	insert50 ≝
		SELECT
			((Status = 1) ∧ (AskChange = 0) ∧ (AskCoffee = 0) ∧ ((MAX_BAL > (Balance + 50)) ∨ (MAX_BAL = (Balance + 50))))
		THEN
			Balance := (Balance + 50)
		END

	insert100 ≝
		SELECT
			((Status = 1) ∧ (AskChange = 0) ∧ (AskCoffee = 0) ∧ ((MAX_BAL > (Balance + 100)) ∨ (MAX_BAL = (Balance + 100))))
		THEN
			Balance := (Balance + 100)
		END

	powerUp ≝
		SELECT
			((Status = 0) ∧ (CoffeeLeft > 0) ∧ (Pot > 0) ∧ ((MAX_POT > Pot) ∨ (MAX_POT = Pot)))
		THEN
			Status := 1 ||
			Balance := 0 ||
			AskCoffee := 0 ||
			AskChange := 0
		END

	powerDown ≝
		SELECT
			(((Status = 1) ∧ (AskChange = 0) ∧ (AskCoffee = 0) ∧ (Balance = 0)) ∨ (Status = 2))
		THEN
			Status := 0
		END

	autoOut ≝
		SELECT
			(Status = 1)
		THEN
			Status := 2
		END

	takePot ≝
		SELECT
			(Status = 0)
		THEN
			CHOICE
				Pot := 200
			OR
				Pot := 100
			END
		END

	coffeeReq ≝
		SELECT
			((Status = 1) ∧ ((Balance > 50) ∨ (Balance = 50)) ∧ (AskCoffee = 0) ∧ (AskChange = 0))
		THEN
			AskCoffee := 1
		END

	changeReq ≝
		SELECT
			((Status = 1) ∧ (Balance > 0) ∧ (AskCoffee = 0) ∧ (AskChange = 0))
		THEN
			AskChange := 1
		END

	addCoffee ≝
		ANY
			x
		WHERE
			((x > 0) ∧ ((MAX_COFFEE > x) ∨ (MAX_COFFEE = x)) ∧ ((MAX_COFFEE > (CoffeeLeft + x)) ∨ (MAX_COFFEE = (CoffeeLeft + x))))
		THEN
			SELECT
				((Status = 0) ∧ (MAX_COFFEE > CoffeeLeft))
			THEN
				CoffeeLeft := (CoffeeLeft + x)
			END
		END

	serveCoffee ≝
		SELECT
			((Status = 1) ∧ ((Balance > 50) ∨ (Balance = 50)) ∧ (AskCoffee = 1) ∧ (CoffeeLeft > 0))
		THEN
			AskCoffee := 0 ||
			Balance := (Balance - 50) ||
			CoffeeLeft := (CoffeeLeft - 1) ||
			Pot := (Pot + 50) ||
			IF
				(((Pot + 50) > MAX_POT) ∨ (CoffeeLeft = 1))
			THEN
				Status := 2
			ELSE
				SKIP
			END ||
			IF
				(Balance > 50)
			THEN
				AskChange := 1
			ELSE
				SKIP
			END
		END

	changeBack ≝
		SELECT
			((Status = 1) ∧ (Balance > 0) ∧ (AskChange = 1))
		THEN
			Balance := 0 ||
			AskChange := 0
		END
