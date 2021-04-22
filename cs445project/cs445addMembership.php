<?php

require "cs445conn.php";

$orgname = $_POST["orgName"];
$userid = $_POST["userid"];

$mysqli_qry ="Select id from organization where name like '$orgname'";
$result = mysqli_query($conn,$mysqli_qry);
 
if(mysqli_num_rows($result)>0){
    while($row = mysqli_fetch_assoc($result)){
        $orgid = $row['id'];
        $mysqli_qry2 ="INSERT into membership (userid, organizationid) values ('$userid', '$orgid')";
        
        $result2 = mysqli_query($conn,$mysqli_qry);
 
        if($conn->query($mysqli_qry2) === TRUE){
            echo "Welcome New Member";
        }else{
            echo "Registration Failed";
        }
    }
}else{
    echo "Requested Organization Does Not Exist";
}

$conn->close();
?>