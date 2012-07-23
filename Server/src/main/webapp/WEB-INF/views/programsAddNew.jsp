<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Add Program</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Add Program</h1>

<fieldset>
<legend>New Program</legend>
<form:form modelAttribute="prog" method="POST" commandName="prog">
	<p><form:errors path="*" cssStyle="color : red;"/></p>
	<p>
		<form:errors path="name" />
		<form:label path="name" for="name">Program Name:</form:label>
		<form:input path="name" />
	</p>
	<p>
		<input name="send" type="submit" value="Add Program" />
	</p>
</form:form>
</fieldset>

</body>
</html>