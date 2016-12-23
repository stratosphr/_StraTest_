MACHINE
	CreditCard

SETS
	/* L'ensemble des messages echanges entre les composants */
	SIGNALS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
	/* Les etats possibles pour le terminal */
	ATM_STATES = {22,23,24,25,26,27,28,29,30,31,32,33,34,35,36};
	/* Les etats possibles pour la carte */ 
	CARD_STATES = {37,38,39,40,41,42};
	/* Les etats possibles pour la banque (base de donnees) */
	DB_STATES = {43,44,45,46,47,48};
	MESBOOL = {49,50}

VARIABLES
	/* Stocke l'etat courant du systeme (variable de controle) */
	ATM_state, CARD_state, DB_state,
	/* L'id de la carte, son pin et le pin de l'utilisateur recu par la carte */
	C_card_id, C_card_pin, C_user_pin,
	/* Le compteur de tentatives de verification du pin */
	C_counter, 
	/* Le solde du compte associe a la carte, l'id de la carte stocke par la banque, l'id de la carte recu par la banque,
		le type d'erreur rencontre par la banque, le retrait demande par l'utilisateur envoye a la banque */
	DB_balance, DB_count_id, DB_card_id, DB_error, DB_asked_withdrawal,
	/* Le resultat de l'identification du pin recu par le terminal, le message d'erreur emis par la banque recu par le terminal, le pin entre par l'utilisateur, l'id de la carte lu par l'ATM, le solde du compte recu par l'ATM, le retrait demande par l'utilisateur recu par l'ATM, le solde du compte calcule par l'ATM apres l'operation de retrait */
	A_result, A_error, A_user_pin, A_card_id, A_balance, A_asked_withdrawal, A_balance_after_operation, out

INVARIANT
	ATM_state : ATM_STATES & CARD_state : CARD_STATES & DB_state : DB_STATES & out : SIGNALS &
	C_card_id : 0..9999 & C_card_pin : 0..9999 & C_user_pin : 0..9999 &
	C_counter : 0..3 & 
	DB_balance : 0..10000 & DB_count_id : 0..9999 & DB_card_id : 0..9999 & DB_error : 0..3 & DB_asked_withdrawal : 0..10000 &
	A_result : MESBOOL & A_error : 0..3 & A_user_pin : 0..9999 & A_card_id : 0..9999 & A_balance : 0..10000 & A_asked_withdrawal : 0..10000 & A_balance_after_operation : 0..10000 &
	((ATM_state = 32 or ATM_state = 33 or ATM_state = 34) => (A_balance = DB_balance))

INITIALISATION
	ATM_state := 22 || CARD_state := 37 || DB_state := 43 ||
	C_card_id := 3121 || C_card_pin := 4040 || C_user_pin := 1 || 
	C_counter := 1 || 
	DB_balance := 7400 || DB_count_id := 3121 || DB_card_id := 1 || DB_error := 1 || DB_asked_withdrawal := 1 ||
	A_result := 50 || A_error := 1 || A_user_pin := 1 || A_card_id := 1 || A_balance := 1 || A_asked_withdrawal := 1 || A_balance_after_operation := 1 || out := 1

OPERATIONS
	/* Le terminal recoit le signal de connexion de la carte */
	ATM_signal_connexion =
		SELECT
			ATM_state = 22
		THEN
			out := 1 ||
			ATM_state := 23
		END;

	ATM_operation_vide =
		SELECT
			ATM_state = 23
		THEN
			ATM_state := 24
		END;

	/* Le terminal demande son identifiant a la carte */
	ATM_demande_id =
		SELECT
			ATM_state = 24 & CARD_state = 37
		THEN
			IF 
				C_card_id < 12345 & C_card_id <= 4823 & C_card_pin < 4545 & C_card_id <= 9114
			THEN
				out := 2 ||
				C_counter := 2 ||
				CARD_state := 38 || ATM_state := 25
			END
		END;

	/* La carte envoie son identifiant au terminal */
	CARD_reponse_id =
		SELECT
			ATM_state = 25 & CARD_state = 38
		THEN
			out := 3 ||
			A_card_id := C_card_id ||
			ATM_state := 26 || CARD_state := 39
		END;

	/* Le terminal demande le code pin de l'utilisateur */
	ATM_demande_pin_user =
		SELECT
			ATM_state = 26
		THEN
			ATM_state := 27 ||
			out := 5
		END;

	/* L'utilisateur envoie son code pin au terminal */
	ATM_recoit_pin_user =
		SELECT
			ATM_state = 27
		THEN
			out := 4 ||
			ANY xx WHERE xx : 0..9999 & (xx = 4040 or xx = 2121) THEN A_user_pin := xx END ||
			ATM_state := 28
		END;

	/* Le terminal transfere le pin recu de l'utilisateur a la carte */
 	ATM_transfer_pin_user =
		SELECT
			ATM_state = 28 & CARD_state = 39
		THEN
			out := 7 ||
			C_user_pin := A_user_pin ||
			ATM_state := 29 || CARD_state := 40
		END;

	/* La carte traite le pin recu */
	CARD_traite_pin =
		SELECT
			ATM_state = 29 & CARD_state = 40
		THEN
			IF 
				/* Le nombre d'essais maximums n'est pas atteint mais le pin donne est mauvais */
				(C_counter < 3) & not(C_user_pin = C_card_pin)
			THEN
				out := 8 ||
				A_result := 50 ||
				C_counter := C_counter + 1 ||
				CARD_state := 39 || ATM_state := 30
			ELSE
				IF
					/* Le nombre d'essais maximums n'est pas atteint et le pin donne est correct */
					(C_counter <= 3) & (C_user_pin = C_card_pin)
				THEN
					CARD_state := 41
				ELSE
					IF
						/* Le nombre d'essais maximums est atteint et le pin est mauvais */
						(C_counter >= 3) & not(C_user_pin = C_card_pin)
					THEN
						CARD_state := 42
					END
				END
			END
		END;

	/* L'authentification a reussie, la carte le transmet au terminal */
	CARD_succes_pin = 
		SELECT			
			CARD_state = 41 & ATM_state = 29
		THEN
			out := 8 ||
			A_result := 49 ||
			CARD_state := 37 || ATM_state := 30
		END;

	/* L'authentification a echoue, la carte le transmet au terminal */
	CARD_failed_pin =
		SELECT
			CARD_state = 42 & ATM_state = 29
		THEN
			out := 9 ||
			CARD_state := 37 || ATM_state := 22
		END;

	/* La carte traite le resultat de l'authentification */
	ATM_traite_reponse_pin =
		SELECT
			ATM_state = 30 & DB_state = 43
		THEN
			IF
				/* Si l'authentification a reussi, le terminal transmet l'identifiant de la carte a la banque */
				A_result = 49 & (DB_balance >= 5000) & (DB_balance <= 10000)
			THEN
				out := 10 ||
				DB_card_id := A_card_id ||
				ATM_state := 31 || DB_state := 44
			ELSE
				/* Si l'authentification a echoue, le terminal demande un nouveau pin a l'utilisateur */
				out := 11 ||
				ATM_state := 27
			END
		END;

	/* La banque verifie l'identifiant recu */
	DB_check_id =
		SELECT
			DB_state = 44
		THEN
			IF
				DB_card_id = DB_count_id
			THEN
				DB_state := 45
			ELSE
				DB_error := 2 ||
				DB_state := 48
			END
		END;

	/* Si l'identifiant de la carte n'est pas le bon ou si le montant demande est trop grand par rapport au solde, la banque annule l'operation */
	DB_operation_not_done =
		SELECT
			DB_state = 48 & (ATM_state = 31 or ATM_state = 36)
		THEN
			out := 18 ||
			A_error := DB_error ||
			ATM_state := 22 || DB_state := 43
		END;

	/* La banque demande au terminal le montant du retrait */
	DB_demande_retrait =
		SELECT
			DB_state = 45 & ATM_state = 31
		THEN
			A_balance := DB_balance ||
			out := 12 ||
			ATM_state := 32 || DB_state := 46
		END;

	/* Le terminal demande a l'utilisateur le montant du retrait */
	ATM_demande_retrait =
		SELECT
			ATM_state = 32
		THEN
			ATM_state := 33 ||
			out := 13
		END;

	/* Le terminal recoit le montant du retrait demande par l'utilisateur */
	ATM_obtient_montant =
		SELECT
			ATM_state = 33
		THEN
			out := 14 ||
			ANY xx WHERE xx : 0..10000 THEN A_asked_withdrawal := xx END ||
			ATM_state := 34
		END;

	/* Le terminal transmet le montant du retrait a la banque si celui-ci est conforme au solde */
	ATM_traite_retrait =
		SELECT
			ATM_state = 34 & DB_state = 46
		THEN
			IF
				A_balance >= A_asked_withdrawal
			THEN
				A_balance_after_operation := A_balance - A_asked_withdrawal ||
				DB_balance := DB_balance - A_asked_withdrawal ||
				DB_asked_withdrawal := A_asked_withdrawal ||
				out := 15 ||
				DB_state := 47 || ATM_state := 35
			ELSE
				out := 16 ||
				DB_error := 3 ||
				ATM_state := 36 || DB_state := 48
			END
		END;

	/* Le traitement s'est bien deroule, la banque transmet son accord au terminal */
	DB_traitement_ok =
		SELECT
			DB_state = 47 & ATM_state = 35
		THEN
			out := 17 ||
			DB_state := 43 || ATM_state := 22
		END
END
