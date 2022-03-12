document.addEventListener('DOMContentLoaded', function () {
	var btnSubmit = document.getElementById('btn-submit'); 
	var txtName = document.forms['product-form']['name']; 
	var txtEmail = document.forms['product-form']['email']; 
	var txtStatus = document.forms['product-form']['status']; 

	
	var url_string = window.location.href.toLowerCase();
	var url = new URL(url_string);
	var id = url.searchParams.get('id');
	var isEdit = false;
	if (id != undefined && id.length > 0) {
		isEdit = true;
		let xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = function () {
			if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
				var data = JSON.parse(xmlHttpRequest.responseText);
				console.log(data);
				txtName.value = data.name;
				txtEmail.value = data.email;
				txtStatus.value = data.status;
			}
		};
		xmlHttpRequest.open(
			'get',
			'http://localhost:8080/api/employees/' + id,
			false
		);
		xmlHttpRequest.send();
	}

	btnSubmit.onclick = function () {
		var name = txtName.value;
		var email = txtEmail.value;
		var status = txtStatus.value;
		var dataToSend = {
			name: name,
			email: email,
			status: status,
		};

		var method = 'post';
		var url = 'http://localhost:8080/api/employees';
		var successStatus = 201;
		if (isEdit) {
			method = 'put';
			url = `${url}/${id}`;
			successStatus = 200;
		}
		var xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = function () {
			if (
				xmlHttpRequest.readyState == 4 &&
				xmlHttpRequest.status == successStatus
			) {
				alert('Create employee success!');
				window.location.href = './index.html';
			}
		};
		xmlHttpRequest.open(method, url, false);
		xmlHttpRequest.setRequestHeader('Content-Type', 'application/json');
		xmlHttpRequest.send(JSON.stringify(dataToSend)); // gửi dữ liệu ở định dạng json.
	};
});