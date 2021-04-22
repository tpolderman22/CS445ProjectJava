<?php

require "cs445conn.php";

$userid = $_POST["userid"];
//$organizationid = $_POST["organizationid"];

$mysqli_qry ="Select organizationid from membership where userid like '$userid'";
$result = mysqli_query($conn,$mysqli_qry);
 
if(mysqli_num_rows($result)>0){

    echo "Membership Check Success,";

    while($row = mysqli_fetch_assoc($result)){
        $orgid = $row['organizationid'];
        $mysqli_qry2 = "select name, id from organization where id like '$orgid'";
        $orgResult = mysqli_query($conn,$mysqli_qry2);
        while($orgRow = mysqli_fetch_assoc($orgResult))
            echo $orgRow['id'] . "@" . $orgRow['name'] . ";";
    }
}else{
    echo "No Memberships";
}

$conn->close();

?>