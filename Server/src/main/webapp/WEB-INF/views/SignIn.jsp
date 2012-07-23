<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Sign in</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Sign in! </h1>
<fieldset>
<legend>Sign in</legend>

<form method="post" action="<c:url value="/j_spring_security_check" />">
	<c:if test="${not empty param.error}"> 
		<font color="red"> 
			Login error. <br /> 
	  		Reason : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
		</font>
	</c:if>
	<p>
		<label for="j_username">Username:</label>
		<input name="j_username" />
	</p>
	<p>
		<label for="j_password">Password:</label>
		<input name="j_password" type="password" />
	</p>
	<p>
		<label for="_spring_security_remember_me">Remember me:</label>
		<input type="checkbox" name="_spring_security_remember_me" />
	</p>
	<p>
		<input name="send" type="submit" value="Sign in" />
	</p>
</form>
</fieldset>
</body>
</html>
