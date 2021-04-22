<?php

require "cs445conn.php";

$username = $_POST["username"];
$password = $_POST["password"];

$mysqli_qry ="Select * from user where email like '$username' and password like '$password'";
$result = mysqli_query($conn,$mysqli_qry);
 
if(mysqli_num_rows($result)>0){
    echo "User Already Exists";
}else{
    $mysqli_qry ="INSERT into user (email, password) values ('$username', '$password')";
    $result = mysqli_query($conn,$mysqli_qry);
 
    if($conn->query($mysqli_qry) === TRUE){
        echo "Registration Success";
    }else{
        echo "Registration Failed";
    }
}

$conn->close();
?>