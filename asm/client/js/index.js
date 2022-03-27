document.addEventListener('DOMContentLoaded', function () {
    var tableBody = document.getElementById('my-table-data');
    var xmlHttpRequest = new XMLHttpRequest();
    var data = undefined;

    xmlHttpRequest.onreadystatechange = function () {
        if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
            data = JSON.parse(xmlHttpRequest.responseText); 
            var newContent = '';
            for (let i = 0; i < data.length; i++) {
                newContent += `
                <tr>
                	<td class="product-id">${data[i].id}</td>
                    <td class="prodct-name">${data[i].name}</td>
                    <td class="price">${data[i].price}</td>
                    <td>${data[i].status}</td>
                    <td>
                        <button class="w3-button w3-blue"><a href="form.html?id=${data[i].id}" class="btn-edit"><i class="fa fa-pencil" aria-hidden="true"></i></a> </button>|
                        <button class="w3-button w3-red"><a href="#" title="${data[i].id}" class="btn-delete"><i class="fa fa-trash" aria-hidden="true"></i></a></button>	|
                        <button id="add-cart-${data[i].id}"  class="w3-button w3-aqua"><a href="#" title="${data[i].id}" class="btn-cart"><i class="fa fa-cart-plus" aria-hidden="true"></i></a></button>
                    </td>
                </tr>`;
            }
            tableBody.innerHTML = newContent;
        }
    };
    xmlHttpRequest.open('get', 'http://localhost:8080/api/products', false);
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
                    'http://localhost:8080/api/products/' + id,
                    false
                );
                xmlHttpRequest.send();
            }
        }
    });

    var url = 'http://localhost:8080/api/carts';
    var method = 'get'
    var xmlRequest = new XMLHttpRequest();
    var modal = document.getElementById("myModal");
    var close = document.getElementsByClassName("close")[0];
    var modalbody = document.getElementById('cart-items');
    var totalPrice = document.getElementById('total-price')
    var btnGetCart = document.getElementById("cart");
    var close_footer = document.getElementsByClassName("close-footer")[0];
    //Get cart
    var cartData = undefined
    btnGetCart.onclick = function () {
        modal.style.display = "block";
    }

        xmlRequest.onreadystatechange = function () {
            if (xmlRequest.readyState == 4 && xmlRequest.status == 200) {
                cartData = JSON.parse(xmlRequest.responseText);
                var content = '';
                for (let i = 0; i < cartData.cartItems.length; i++) {
                    content += ` <div class="cart-row">
                                        <div class="cart-item cart-column">
                                            <span class="qcart-item-title">${cartData.cartItems[i].productName}</span>
                                        </div>
                                        <span class="cart-price cart-column">${cartData.cartItems[i].unitPrice}</span>
                                        <div class="cart-quantity cart-column">
                                            <input class="cart-quantity-input" type="number" value="${cartData.cartItems[i].quantity}">
                                            <button id="btn-remove-${cartData.cartItems[i].productId}"  class="btn btn-danger" type="button">Xóa</button>
                                        </div>
                                    </div>`;
                }
                modalbody.innerHTML = content;
                totalPrice.innerHTML = cartData.totalPrice
            }
        }
        xmlRequest.open(method, url, false);
        xmlRequest.setRequestHeader('Authorization', '1');
        xmlRequest.send();

    close.onclick = function () {
        modal.style.display = "none";
    }
    close_footer.onclick = function () {
        modal.style.display = "none";
    }
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
    //add to cart
    for (let i = 0; i < data.length; i++) {
        var addBtn = document.getElementById(`add-cart-${data[i].id}`);
        addBtn.onclick = function (){
            console.log(data[i].id);
            console.log(data)
            xmlRequest.onreadystatechange = function (){
                if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                    alert("Sản phẩm đã được thêm vào giỏ hàng")
                }
            }
            xmlRequest.open(method, url + `/add?productId=${data[i].id}&quantity=1`, false);
            xmlRequest.setRequestHeader('Authorization', '1');
            xmlRequest.send();
        }
    }
    //delete cart
    var btnPayment = document.getElementById('btn-payment');
    btnPayment.onclick = function (){
        xmlRequest.onreadystatechange = function (){
            if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                window.location.reload();
                alert("Thanh Toán Thành công")
            }
        }
        xmlRequest.open(method, url + `/clear`, false);
        xmlRequest.setRequestHeader('Authorization', '1');
        xmlRequest.send();
    }
    //remove item
    for (let i = 0; i < cartData.cartItems.length; i++) {
        var removeBtn = document.getElementById(`btn-remove-${cartData.cartItems[i].productId}`);
        removeBtn.onclick = function (){
            console.log(cartData.cartItems[i].productId);
            xmlRequest.onreadystatechange = function (){
                if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                    window.location.reload();
                    alert("xóa thành công")
                }
            }
            xmlRequest.open(method, url + `/remove?productId=${cartData.cartItems[i].productId}`, false);
            xmlRequest.setRequestHeader('Authorization', '1');
            xmlRequest.send();
        }
    }
console.log(cartData)

});
