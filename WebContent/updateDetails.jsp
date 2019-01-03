<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h1>Update Account Details</h1>
	<form action="updateAccount.mm">
		<label>Account Number</label>
		<input type="text" name="accountnumber" readonly="readonly" value="${requestScope.accounts.bankAccount.accountNumber}"><br><br>
		<label>AccountHolderName</label>
		<input type="text" name="accountHolderName" value="${requestScope.accounts.bankAccount.accountHolderName}"><br><br>
		<label>Account Balance</label>
		<input type="text" name="accountBalance" readonly="readonly" value="${requestScope.accounts.bankAccount.accountBalance}"><br><br>
		<label>salary</label>
		<input type="radio" name="salary" value="Yes" ${requestScope.accounts.salary==true?"checked":""}>Yes
		<input type="radio" name="salary" value="No" ${requestScope.accounts.salary==true?"":"checked"}>No<br><br>
		<input type="submit" value="Update Account">
		<input type="reset" value="Reset">
	</form>
</body>
</html> 