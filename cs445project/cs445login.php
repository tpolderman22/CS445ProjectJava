<?php

require "cs445conn.php";

$username = $_POST["username"];
$password = $_POST["password"];

$mysqli_qry ="Select userid from user where email like '$username' and password like '$password'";
$result = mysqli_query($conn,$mysqli_qry);
 
if(mysqli_num_rows($result)>0){
    while($row = mysqli_fetch_assoc($result)){
        echo $row['userid'] . " Login Success";
    }
}else{
    echo "Login Failed";
}

$conn->close();

?>