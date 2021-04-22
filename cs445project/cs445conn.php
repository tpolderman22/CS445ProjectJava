<?php
 
// Create connection
$dbName = "cs445_project";
$access_username = "tpolderman";
$access_password = "PRSce24!";
$servername = "localhost";

$conn=mysqli_connect($servername,$access_username,$access_password,$dbName);
 
// // Check connection
// if (mysqli_connect_errno())
// {
//   echo "Failed to connect to MySQL: " . mysqli_connect_error();
// }else{
//   echo "Connection Success!";
// }
 
?>