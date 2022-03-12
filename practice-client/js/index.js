document.addEventListener('DOMContentLoaded', function () {
	var tableBody = document.getElementById('my-table-data');
	var xmlHttpRequest = new XMLHttpRequest();
	xmlHttpRequest.onreadystatechange = function () {
		if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
			var data = JSON.parse(xmlHttpRequest.responseText);
			var newContent = '';
			for (let i = 0; i < data.length; i++) {
				newContent += `
                <tr>
                    <td>${data[i].name}</td>
                    <td>${data[i].email}</td>
                    <td>${data[i].status}</td>
                    <td>
                        <a href="./form.html?id=${data[i].id}" class="btn-edit">Edit</a> 
                        <a href="#" title="${data[i].id}" class="btn-delete">Delete</a>
                    </td>
                </tr>`;
			}
			tableBody.innerHTML = newContent; 
		}
	};
	xmlHttpRequest.open('get', 'http://localhost:8080/api/employees', false);
	xmlHttpRequest.send();

	document.body.addEventListener('click', function (event) {
		if (event.target.className === 'btn-delete') {
			if (confirm('Are you sure you want to delete?')) {
				let id = event.target.title;
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = function () {
					if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
						alert('Deleted successfully');
						location.reload();
					}
				};
				xmlHttpRequest.open(
					'delete',
					'http://localhost:8080/api/employees/' + id,
					false
				);
				xmlHttpRequest.send();
			}
		}
	});
});