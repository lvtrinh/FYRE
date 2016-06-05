<?php
ini_set('display_errors', 1);
/**
 * This file contains functions that perform queries on the MYSQL database
 */

class DB_Functions {

    private $conn;

    /**************************************************************************
     * _construct()
     *
     * This is the DB_Functions constructor which sets up the connection to the MYSQL
     * database
     *
     **************************************************************************/
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    /**************************************************************************
     * _destructor()
     *
     * This is the DB_Functions destructor
     *
     **************************************************************************/
    function __destruct() {

    }

   /**************************************************************************
     * storeUser()
     *
     * This function stores a row into the MYSQL users table
     *
     * @param name The user's name
     * @param email The user's email
     * @param password The user's password
     * @param security_question The security question the user selected in registration
     * @param security_answer The answer to the security question
     * @return The values of the newly inserted row into the users table
     **************************************************************************/
    public function storeUser($name, $email, $password, $security_question, $security_answer) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at, security_question, security_answer) VALUES(?, ?, ?, ?, ?, NOW(), ?, ?)");
        $stmt->bind_param("sssssis", $uuid, $name, $email, $encrypted_password, $salt, $security_question, $security_answer);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }

    /**************************************************************************
     * storeReceipt()
     *
     * This function stores a row into the MYSQL receipts table
     *
     * @param id The user's id in the users table
     * @param storeName The name of the store
     * @param storeStreet The street address of the store
     * @param storeCityState The city, state, and zip code of the store
     * @param storePhone The phone number of the store
     * @param storeWebsite The website of the store
     * @param storeCategory The category of the store
     * @param hereGo Whether the order was here or to-go
     * @param cardType The type of the card used (e.g. Visa, MasterCard, etc.)
     * @param carNum The last 4 digits of the card number
     * @param paymentMethod The type of payment (e.g. cash, swipe, insert, etc.)
     * @param tax The tax amount
     * @param subtotal The subtotal amount
     * @param totalPrice The total amount
     * @param date The date of purchase
     * @param time The time of purchase
     * @param cashier The cashier in charge of the transaction
     * @param checkNumber The check number of the receipt
     * @param orderNumber The order number of the receipt
     * @param memo The memo associated with the receipt
     * @return The values of the newly inserted row into the receipts table
     **************************************************************************/
    public function storeReceipt($id, $storeName, $storeStreet, $storeCityState, $storePhone, $storeWebsite, $storeCategory, $hereGo, $cardType, $cardNum, $paymentMethod, $tax, $subtotal, $totalPrice, $date, $time, $cashier, $checkNumber, $orderNumber, $memo) {
		// Prepare the statement
		$stmt = $this->conn->prepare("INSERT INTO receipts(id, store_name, store_street, store_city_state, store_phone, store_website, store_category, here_go, card_type, card_num, payment_method, subtotal, tax, total_price, date, time, cashier, check_number, order_number, memo) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		//Bind parameters and execute
		$stmt->bind_param("issssssisisdddssssis", $id, $storeName, $storeStreet, $storeCityState, $storePhone, $storeWebsite, $storeCategory, $hereGo, $cardType, $cardNum, $paymentMethod, $tax, $subtotal, $totalPrice, $date, $time, $cashier, $checkNumber, $orderNumber, $memo);
		$result = $stmt->execute();
    	$stmt->close();

    	// check for successful store
        if ($result) {
        	//Receipt successfully stored
            $stmt = $this->conn->prepare("SELECT * FROM receipts WHERE id=? ORDER BY receipt_id DESC LIMIT 1");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $receipt = $stmt->get_result()->fetch_assoc();
            $stmt->close();

 			//Return the id of receipt from the receipts table
            return $receipt;
        } else {
            return false;
        }
	}

	/**************************************************************************
     * storeItem()
     *
     * This function stores a row into the MYSQL items table
     *
     * @param receiptId The id of the receipt in the receipts table
     * @param itemName The name of the item
     * @param itemDescription A description of the item
     * @param price The price of the item
     * @param itemNum The number associated with the item in the store's inventory
     * @param quantity The amount of this item that was purchased
     * @param taxType The tax-type of the item
     * @return The values of the newly inserted row into the items table
     **************************************************************************/
	public function storeItem($receiptId, $itemName, $itemDescription, $price, $itemNum, $quantity, $taxType) {
	//public function storeItem($receiptId, $itemName, $itemDescription) {
		//Prepare the statement
		$stmt = $this->conn->prepare("INSERT INTO items(receipt_id, item_name, item_description, price, item_num, quantity, tax_type) VALUES(?, ?, ?, ?, ?, ?, ?)");

		//Bind parameters and execute
		$stmt->bind_param("issdiis", $receiptId, $itemName, $itemDescription, $price, $itemNum, $quantity, $taxType);
		$result = $stmt->execute();
		$stmt->close();

		//Check for succesful store
		if ($result) {
			//Get the details of the item
			$stmt = $this->conn->prepare("SELECT * FROM items WHERE receipt_id=? ORDER BY item_id DESC LIMIT 1");
			$stmt->bind_param("i", $receiptId);
            $stmt->execute();
            $item = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $item;
		} else {
			return false;
		}
	}

	/**************************************************************************
     * getReceipts()
     *
     * This function returns all the rows in the receipts table associated with a particular
     * user id
     *
     * @param id The user's id from the users table
     * @return An array containing all the receipts. Each receipt's item list is set to
     * null.
     **************************************************************************/
	public function getReceipts($id) {
		//Prepare the statement
		$stmt = $this->conn->prepare("SELECT * FROM receipts WHERE id=? ORDER BY date DESC");

		//Bind parameters and execute
		$stmt->bind_param("i", $id);
		$result = $stmt->execute();

		if ($result) {
			//Succesfully retrieved the data
			$data = $stmt->get_result();
			$stmt->close();

			//Create an array to store all the receipts
			$receipts = [];

			//While there are still more receipts, get the associative array for the receipt
			while ($row = $data->fetch_assoc()) {
				//Array to hold the details of the receipt
				$receipt = $row;
				$receipt["items"] = null;

    			//Put this receipt into the receipts array
    			$receipts[] = $receipt;
			}

			//Return the array holding all the receipts
			return $receipts;
		} else {
			//An error occurred in retrieval
			$stmt->close();
			return false;
		}
	}

	/**************************************************************************
     * getReceiptItems()
     *
     * This function returns all the items in the items table associated with a particular
     * receipt
     *
     * @param receiptId The id of the receipt in the receipts table
     * @return An array containing all the items for a particular receipt
     **************************************************************************/
	public function getReceiptItems($receiptId) {

		//Prepare the statement
		$stmt = $this->conn->prepare("SELECT * FROM items WHERE receipt_id=?");

		//Bind parameters and execute
		$stmt->bind_param("i", $receiptId);
		$result = $stmt->execute();

		if ($result) {
			//Succesfully retrieved the data
			$data = $stmt->get_result();
			$stmt->close();

			//Create an array to store all the receipts
			$items = array();

			//While there are still more items, get the associative array for the receipt
			while ($row = $data->fetch_assoc()) {
				//Array to hold the details of the receipt
				$item = array();

				//Populate the receipt array
				$item["itemId"] = $row["item_id"];
				$item["receiptId"] = $row["receipt_id"];
    			$item["itemName"] = $row["item_name"];
    			$item["itemDescription"] = $row["item_description"];
    			$item["price"] = $row["price"];
    			$item["itemNum"] = $row["item_num"];
    			$item["quantity"] = $row["quantity"];
    			$item["taxType"] = $row["tax_type"];

    			//Put this receipt into the receipts array
    			array_push($items, $item);
			}

			//Return the array holding all the receipts
			return $items;
		} else {
			//An error occurred in retrieval
			$stmt->close();
			return false;
		}
	}


    /**************************************************************************
     * getUserEmailAndPassword()
     *
     * This function gets a user by their email and password.
     *
     * @param email The user's email
     * @param password The user's password
     * @return Returns the values of a user if the password is a match for the email.
     * Otherwise, returns false.
     **************************************************************************/
    public function getUserByEmailAndPassword($email, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }


    /**************************************************************************
     * getSecurityQuestion()
     *
     * This function gets a users security question
     *
     * @param email The user's email
     * @return Returns the users email and security question
     **************************************************************************/
    public function getSecurityQuestion($email) {

        $stmt = $this->conn->prepare("SELECT email,security_question FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $result = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $result;
        } else {
            return false;
        }
    }

    /**************************************************************************
     * getSecurityDetails()
     *
     * This function gets a users security question and security answer
     *
     * @param email The user's email
     * @return Returns the users security question and security answer
     **************************************************************************/
    public function getSecurityDetails($email) {

        $stmt = $this->conn->prepare("SELECT security_question,security_answer FROM users WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $result = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $result;
        } else {
            return false;
        }
    }

    /**************************************************************************
     * changeSecurityPreferences()
     *
     * This function a user's security question and security answer
     *
     * @param id The user's id in the users table
     * @param security_question The user's new security question
     * @param security_answer The user's new security answer
     * @return Returns the values for the row in the users table with id
     **************************************************************************/
    public function changeSecurityPreferences($id, $security_question, $security_answer) {
    	$stmt = $this->conn->prepare("UPDATE users SET security_question=?,security_answer=? WHERE id=?");

 		$stmt->bind_param("isi", $security_question, $security_answer, $id);
 		$result = $stmt->execute();
 		$stmt->close();

 		if ($result) {
 			$stmt = $this->conn->prepare("SELECT * FROM users WHERE id=?");
 			$stmt->bind_param("i", $id);
            $stmt->execute();
            $update = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $update;
		} else {
			return false;
 		}
    }


    /**************************************************************************
     * changeMemo()
     *
     * This function update's the memo for a receipt
     *
     * @param receipt_id The receipt id
     * @param memo The new memo
     * @return Returns the values for the row in the receipts table associated with
     * receipt_id
     **************************************************************************/
    public function changeMemo($receipt_id, $memo) {
    	$stmt = $this->conn->prepare("UPDATE receipts SET memo=? WHERE receipt_id=?");

 		$stmt->bind_param("si", $memo, $receipt_id);
 		$result = $stmt->execute();
 		$stmt->close();

 		if ($result) {
 			$stmt = $this->conn->prepare("SELECT * FROM receipts WHERE receipt_id=?");
 			$stmt->bind_param("i", $receipt_id);
            $stmt->execute();
            $update = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $update;
		} else {
			return false;
 		}
    }


    /**************************************************************************
     * removeUser()
     *
     * This function removes a user from the users table
     *
     * @param email The user's email
     * @return Returns false if the number of rows affected is 0. Otherwise, returns the
     * number of rows affected
     **************************************************************************/
 	public function removeUser($email) {
 		$stmt = $this->conn->prepare("DELETE FROM users WHERE email=?");

 		$stmt->bind_param("s", $email);
 		$result = $stmt->execute();
 		$num_rows = $stmt->affected_rows;
 		$stmt->close();

 		if ($num_rows > 0) {
 			return $num_rows;
		} else {
			return false;
 		}
 	}


	/**************************************************************************
     * updateBasic()
     *
     * This function updates a user's account settings including name and email
     *
     * @param name The user's name
     * @param email The user's email
     * @param id The user's id in the users table
     * @return Returns the name, email and id of the user if the update was succesful.
     * Otherwise, returns false.
     **************************************************************************/
 	public function updateBasic($name, $email, $id) {
 		$stmt = $this->conn->prepare("UPDATE users SET name=?,email=? WHERE id=?");

 		$stmt->bind_param("ssi", $name, $email, $id);
 		$result = $stmt->execute();
 		$stmt->close();

 		if ($result) {
 			$stmt = $this->conn->prepare("SELECT id,name,email FROM users WHERE id=?");
 			$stmt->bind_param("i", $id);
            $stmt->execute();
            $update = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $update;
		} else {
			return false;
 		}
 	}


 	/**************************************************************************
     * updateAll()
     *
     * This function updates a user's account settings uncluding name, email, and password
     *
     * @param name The user's name
     * @param email The user's email
     * @param password The user's password
     * @param id The user's id in the users table
     * @return Returns the name, email and id of the user if the update was succesful.
     * Otherwise, returns false.
     **************************************************************************/
 	public function updateAll($name, $email, $password, $id) {
 		$hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"];

 		$stmt = $this->conn->prepare("UPDATE users SET name=?,email=?,encrypted_password=?,salt=? WHERE id=?");
 		$stmt->bind_param("ssssi", $name, $email, $encrypted_password, $salt, $id);
 		$result = $stmt->execute();
 		$stmt->close();

 		if ($result) {
 			$stmt = $this->conn->prepare("SELECT id,name,email FROM users WHERE id=?");
 			$stmt->bind_param("i", $id);
            $stmt->execute();
            $update = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $update;
		} else {
			return false;
 		}
 	}


 	/**************************************************************************
     * changePassword()
     *
     * This function updates a user's password
     *
     * @param email The user's email
     * @param password The user's password
     * @return Returns the name, email and id of the user if the update was succesful.
     * Otherwise, returns false.
     **************************************************************************/
 	public function changePassword($email, $password) {
 		$hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"];

 		$stmt = $this->conn->prepare("UPDATE users SET encrypted_password=?,salt=? WHERE email=?");

 		$stmt->bind_param("sss", $encrypted_password, $salt, $email);
 		$result = $stmt->execute();
 		$stmt->close();

 		if ($result) {
 			$stmt = $this->conn->prepare("SELECT id,name,email FROM users WHERE email=?");
 			$stmt->bind_param("s", $email);
            $stmt->execute();
            $update = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $update;
		} else {
			return false;
 		}
 	}


    /**************************************************************************
     * isUserExisted()
     *
     * This function checks if a user exists
     *
     * @param email The user's email
     * @return Returns false if the number of rows affected is 0. Otherwise, returns the
     * number of rows affected
     **************************************************************************/
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }


     /**************************************************************************
     * hashSSHA()
     *
     * This function encrypts a user's password
     *
     * @param password The user's password
     * @return Returns the salt and ecnrypted password
     **************************************************************************/
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }


     /**************************************************************************
     * checkhashSSHA()
     *
     * This function decrypt's a user's password
     *
     * @param salt The salt
     * @param password The user's password
     * @return Returns the decrypted password
     **************************************************************************/
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}

?>
