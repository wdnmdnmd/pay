<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>支付</title>
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body>
<div id="qrcode"></div>
<div id="orderId" hidden>${orderId}</div>
<div id="returnUrl"hidden >${returnUrl}</div>
<script type="text/javascript">
    new QRCode(document.getElementById("qrcode"), "${codeUrl}");
    $(function (){
        //计时器
        setInterval(function (){
            console.log('开始查询支付状态...')
            $.ajax({
                url: '/pay/queryByOrderId',
                data:{
                    'id':$('#orderId').text()
                },
                success: function (result){
                    console.log(result)
                    if (result.plateformStatus!=null&&
                        result.plateformStatus==='SUCCESS')
                        location.href=$('#returnUrl').text
                },
                error: function (result){
                    alert(result)
                }
            })
            },2000)
    });
</script>
</body>
</html>