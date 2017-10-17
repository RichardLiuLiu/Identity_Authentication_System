<?php
$data = json_encode($_REQUEST);
if(isset($_REQUEST["ssid"]))
{
    $ssid = $_REQUEST["ssid"];
    //echo $ssid ? "On" : "off";
    $fid = fopen($ssid.".txt",'w+');
    fwrite($fid,$data);
    fclose($fid);
    echo "<p>authoring complete</p>";
}
else
{
    echo "<p>you don't give a session id</p>";
}
?>