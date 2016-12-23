/**********************************************************************************************/
/*                                  GSM 11.11 - VERSION LONGUE                                */
/**********************************************************************************************/

MACHINE
	GSM

SETS
	PERMISSION = {1,2,3,4,5};
	VALUE = {6,7};
	FILES = {8,9,10,11,12,13,14,15};
	ENABLED_STATUS = {16, 17};
	BLOCKED_STATUS = {18, 19};
	CODE = {20,21,22,23};
	DATA = {24,25,26,27,28,29};
	STATUS_WORDS = {30,31,32,33,34,35,36}
	
CONSTANTS
	MAX_CHV,
	MAX_UNBLOCK

PROPERTIES
	MAX_UNBLOCK = 10 &
	MAX_CHV = 3

VARIABLES
	PARENT_FILES,
	PERMISSION_READ,
	PERMISSION_UPDATE,
	CODE_UNBLOCK,
	current_file,
	current_directory,
	counter_chv,
	counter_unblock,
	code_chv,
	permission_session,
	enabled_chv1_status,
	blocked_chv_status,
	blocked_status,
	data,
	sw,
	dd_out

INVARIANT
	CODE_UNBLOCK : {2,3} --> CODE &
	CODE_UNBLOCK(2)=22 & CODE_UNBLOCK(3)=23 &
	PARENT_FILES : FILES --> FILES &
	PARENT_FILES(10)=9 & PARENT_FILES(11)=9 & PARENT_FILES(12)=10 & PARENT_FILES(13)=10 & PARENT_FILES(14)=10 & PARENT_FILES(15)=10 &
	PERMISSION_READ : {11,12,13,14,15} --> PERMISSION &
	PERMISSION_READ(11)=1 & PERMISSION_READ(12)=1 & PERMISSION_READ(13)=2 & PERMISSION_READ(14)=2 & PERMISSION_READ(15)=1 &
	PERMISSION_UPDATE : {11,12,13,14,15} --> PERMISSION &
	PERMISSION_UPDATE(11)=5 & PERMISSION_UPDATE(12)=2 & PERMISSION_UPDATE(13)=4 & PERMISSION_UPDATE(14)=2 & PERMISSION_UPDATE(15)=4 &

	/*current_file : FILES - {9,10} &*/
	current_file : {8, 11, 12, 13, 14, 15} &
	/*current_directory : FILES-{8,11,12,13,14,15} &*/
	current_directory : {9, 10} &
	counter_chv : {2,3} --> 0..MAX_CHV &
	counter_unblock : {2,3} --> 0..MAX_UNBLOCK &
	code_chv : {2,3} --> CODE &
	permission_session : PERMISSION --> VALUE & 
	enabled_chv1_status : ENABLED_STATUS &
	blocked_chv_status : {2,3} --> BLOCKED_STATUS &
	blocked_status : {2,3} --> BLOCKED_STATUS &
	data : {11,12,13,14,15} --> DATA &
	(counter_chv(2)=0) <=> (blocked_chv_status(2)=18) &
	(counter_chv(3)=0) <=> (blocked_chv_status(3)=18) &
	(counter_unblock(2)=0) <=> (blocked_chv_status(2)=18) &
	(counter_unblock(3)=0) <=> (blocked_chv_status(3)=18) &
	(current_file = 8 or (PARENT_FILES(current_file) = current_directory)) &
	sw : STATUS_WORDS & dd_out : DATA

INITIALISATION
	current_file := 8 ||
	current_directory := 9 ||
	counter_chv(2):=MAX_CHV ||
	counter_chv(3):=MAX_CHV ||
	counter_unblock(2):=MAX_UNBLOCK ||
	counter_unblock(3):=MAX_UNBLOCK ||
	code_chv(2):=20 ||
	code_chv(3):=21 ||
	enabled_chv1_status := 16 ||
	blocked_chv_status(2):=19 ||
	blocked_chv_status(3):=19 ||
	blocked_status(2):=19 ||
	blocked_status(3):=19 ||
	permission_session(1):=6 ||
	permission_session(2):=7 ||
	permission_session(3):=7 ||
	permission_session(4):=7 ||
	permission_session(5):=7 ||
	data(11):=25 ||
	data(12):=26 ||
	data(13):=27 ||
	data(14):=28 ||
	data(15):=29 ||
	sw := 30 ||
	dd_out := 24

OPERATIONS

	STATUS = 
		BEGIN	
			skip
		END;  


	RESET =
		SELECT 
			(permission_session(1) = 6 or permission_session(1) = 7)
		THEN
			current_file := 8 ||
			current_directory := 9 ||
			permission_session(1):=6 || permission_session(2):=7 || permission_session(3):=7 || permission_session(4):=7 || permission_session(5):=7
		END;


	SELECT_FILE =  
		ANY
			ff
		WHERE
			ff : {8, 9,10,11,12,13,14,15}
		THEN
			IF ((ff=9) or (ff=10)) THEN
				IF ((PARENT_FILES(ff) = current_directory) or (PARENT_FILES(current_directory) = ff) or (ff = 9))
				THEN
					sw := 30 ||
					current_directory := ff ||	
					current_file := 8
				ELSE
					sw := 33
				END
			ELSE 
				IF (PARENT_FILES(ff) = current_directory) 
				THEN
					sw := 30 ||
					current_file := ff
				ELSE
					sw := 33
				END
			END
		END;
			

	READ_BINARY =  
		SELECT
			current_file : {11,12,13,14,15}
		THEN 
			IF (current_file = 8)
			THEN
				sw := 32 
			ELSE
				IF ((permission_session(PERMISSION_READ(current_file)) = 6) or 
					(PERMISSION_READ(current_file)	= 2 & enabled_chv1_status = 17))
				THEN
					sw := 30 ||
					dd_out := data(current_file)
				ELSE
					sw := 34 	
				END
			END 
		END;


	UPDATE_BINARY =  
		ANY
			dd
		WHERE
			dd : {24,25,26,27,28,29} &
			current_file : {11,12,13,14,15}  
		THEN
			IF (current_file = 8)
			THEN
				sw := 32 
			ELSE
				IF ((permission_session(PERMISSION_UPDATE(current_file)) = 6) or
					(PERMISSION_UPDATE(current_file) = 2 & enabled_chv1_status = 17))	 
 				THEN
					sw := 30 ||
					data(current_file) := dd
				ELSE
					sw := 34
				END
			END
		 END;  


	CHANGE_CHV = 
		ANY
			cc, old_code_cc, new_code_cc
		WHERE
			cc : {2,3}  &
			old_code_cc : {20,21,22,23} &
			new_code_cc : {20,21,22,23}
		THEN 
			IF (blocked_chv_status(cc) = 18)
			THEN			
				sw := 36
			ELSE
				IF ((cc=2) & enabled_chv1_status = 17)
				THEN			
					sw := 35
				ELSE
					IF (code_chv(cc)= old_code_cc)
					THEN
						code_chv(cc) := new_code_cc ||
						counter_chv(cc) := MAX_CHV ||
						permission_session(cc) := 6 ||
						sw := 30
					ELSE
						IF (counter_chv(cc) = 1)
						THEN 
							counter_chv(cc) := 0 ||
							blocked_chv_status(cc) := 18 ||
							permission_session(cc) := 7 ||
							sw := 36 
						ELSE
							counter_chv(cc) := counter_chv(cc) - 1 ||
							sw := 34
						END
					END	
				END
			END
		END;


	VERIFY_CHV =  
		ANY
			cc, code_cc
		WHERE
			cc : {2,3} &
			code_cc : {20,21,22,23}
		THEN 
			IF (blocked_chv_status(cc) = 18)
			THEN			
				sw := 36
			ELSE
				IF ((cc=2) & enabled_chv1_status = 17)
				THEN			
					sw := 35
				ELSE
					IF (code_chv(cc) = code_cc)
					THEN
						counter_chv(cc) := MAX_CHV ||
						permission_session(cc) := 6 ||
						sw := 30
					ELSE
						IF (counter_chv(cc) = 1)
						THEN 
							counter_chv(cc) := 0 ||
							blocked_chv_status(cc) := 18 ||
							permission_session(cc) := 7 ||
							sw := 36 
						ELSE
							counter_chv(cc) := counter_chv(cc) - 1 ||
							sw := 34
						END
					END	
				END
			END
		END;		


	UNBLOCK_CHV = 
		ANY
			cc, code_unblock_cc, new_code_cc
		WHERE
			cc : {2,3}  &
			code_unblock_cc : {20,21,22,23} &
			new_code_cc : {20,21,22,23}
		THEN 
			IF (blocked_status(cc) = 18)
			THEN			
				sw := 36
			ELSE
				IF (CODE_UNBLOCK(cc) = code_unblock_cc) 
				THEN
					code_chv(cc) := new_code_cc ||
					blocked_chv_status(cc) := 19 ||
					counter_chv(cc) := MAX_CHV ||
					counter_unblock(cc) := MAX_UNBLOCK ||
					permission_session(cc) := 6 ||
					sw := 30 ||
					IF (cc = 2)
					THEN
						enabled_chv1_status := 16
					END
				ELSE
					IF (counter_unblock(cc) = 1)
					THEN 
						counter_unblock(cc) := 0 ||
						blocked_status(cc) := 18 ||
						sw := 36 
					ELSE
						counter_unblock(cc) := counter_unblock(cc) - 1 ||
						sw := 34
					END
				END	
			END
		END;


	ENABLE_CHV =  
		ANY
			code_cc
		WHERE
			code_cc : {20,21,22,23}
		THEN
			IF (blocked_chv_status(2) = 18)
			THEN
				sw := 36
			ELSE
				IF (enabled_chv1_status = 16)
				THEN
					sw := 35
				ELSE
					IF (code_chv(2) = code_cc)
					THEN
						counter_chv(2) := MAX_CHV ||
						enabled_chv1_status := 16 ||
						permission_session(2) := 6 ||
						sw := 30
					ELSE
						IF (counter_chv(2) = 1)
						THEN 
							counter_chv(2) := 0 ||
							blocked_chv_status(2) := 18 ||
							permission_session(2) := 7 ||
							enabled_chv1_status := 16 ||
							sw := 36 
						ELSE
							counter_chv(2) := counter_chv(2) - 1 ||
							sw := 34
						END
					END
				END
			END	
		END;


	DISABLE_CHV =  
		ANY
			code_cc
		WHERE
			code_cc : {20,21,22,23}
		THEN
			IF (blocked_chv_status(2) = 18)
			THEN
				sw := 36
			ELSE
				IF (enabled_chv1_status = 17)
				THEN
					sw := 35
				ELSE
					IF (code_cc = code_chv(2))
					THEN
						counter_chv(2) := MAX_CHV ||
						enabled_chv1_status := 17 ||
						permission_session(2) := 6 ||
						sw := 30
					ELSE
						IF (counter_chv(2) = 1)
						THEN 
							counter_chv(2) := 0 ||
							blocked_chv_status(2) := 18 ||
							permission_session(2) := 7 ||
							enabled_chv1_status := 16 ||
							sw := 36 
						ELSE
							counter_chv(2) := counter_chv(2) - 1 ||
							enabled_chv1_status := 16 ||
							sw := 34
						END
					END
				END
			END	
		END

END
