<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>스프링프레임워크 게시판</title>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

<script>
    function del(idx) {
        jQuery.ajax({
            type : 'POST' ,
            url : './delete' ,
            data : 'idx=' + idx
        }).done(function(data) {
        	var message = jQuery(data).find("message").text();
            var error = jQuery(data).find("error").text();
            alert(message);
            if (error == 'false') location.reload();
        });

    }
    
    function search() {
        var sch_value = jQuery('#form_search #sch_value').val();
        if (sch_value == '') { alert('검색어를 입력하세요.'); }
        else {
             jQuery('#form_search').submit();
        }
        jQuery('#form_search #sch_type value').val('${mapSearch.sch_type}');
   }
   
    
    </script>
</head>
<body>
	<h1>${message}</h1>
	
	

	<table border="1">
		<colgroup>
			<col width="60">
			<col>
			<col width="115">
			<col width="85">
		</colgroup>
		<thead>
			<tr>
				<th scope="col">기능</th>
				<th scope="col">제목</th>
				<th scope="col">작성자</th>
				<th scope="col">등록일</th>
			</tr>
		</thead>

		<tbody>
			<!-- 목록이 반복될 영역 -->
			<c:forEach var="item" items="${list}" varStatus="status">
				<tr>
					<td><button type="button" onclick="del(${item.idx});">${item.idx}
							삭제</button></td>
					<td><a href="./${item.idx}">${item.subject}</a></td>
					<td>${item.user_name}</td>
					<td>${item.reg_datetime}</td>
				</tr>
			</c:forEach>

		</tbody>

	</table>
	<div>
		<a href="./write">쓰기</a>
	</div>
	<form id="form_search" method="get" action="./">
		<select id="sch_type" name="sch_type">
			<option value="subject" selected="selected">제목</option>
			<option value="content">내용</option>
			<option value="user_name">작성자</option>
		</select> 
		<input type="text" id="sch_value" name="sch_value" value="${mapSearch.sch_value}"/>
		<button type="button" onclick="search();">검색</button>
	</form>
	
	<script>
	function search() {
        var sch_value = jQuery('#form_search #sch_value').val();
        if (sch_value == '') { alert('검색어를 입력하세요.'); }
        else {
             jQuery('#form_search').submit();
        }
   }
   jQuery('#form_search #sch_type value').val('${mapSearch.sch_type}');
   
    
    </script>
</body>
</html>