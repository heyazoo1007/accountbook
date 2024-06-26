let dns = window.location.pathname.split('/')[0];

function initIndex() {
    // httpOnly 로 설정된 Cookie 는 document.cookie 로 읽을 수 없음
    const accessToken = document.cookie.split('access_token=')[1];

    let joinUs = document.getElementById("join-us");
    let logIn = document.getElementById("login");
    let myAccountBook = document.getElementById("my-accountbook");
    let monthly = document.getElementById("monthly");
    let yearly = document.getElementById("yearly");
    let myPage = document.getElementById("my-page");
    let logOut = document.getElementById("logout");

    if(accessToken != null) { // 쿠키에 access_token 이 있을 때(로그인 했을 때)
        joinUs.style.display = 'none';
        logIn.style.display = 'none';

        myAccountBook.style.display = 'inline-block';
        monthly.style.display = 'inline-block';
        yearly.style.display = 'inline-block';
        myPage.style.display = 'inline-block';
        logOut.style.display = 'inline-block';

        getMemberId();
    } else { // 쿠키에 access_token 이 없을 때
        joinUs.style.display = 'inline-block';
        logIn.style.display = 'inline-block';

        myAccountBook.style.display = 'none';
        monthly.style.display = 'none';
        yearly.style.display = 'none';
        myPage.style.display = 'none';
        logOut.style.display = 'none';
    }
}

function getDate() {
    // 한국 표준시간 가져오기
    var date = new Date();
    var utc = date.getTime() + (date.getTimezoneOffset() * 60 * 1000);
    var kstGap = 9 * 60 * 60 * 1000;
    var today = new Date(utc + kstGap);
    var thisMonth = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    // 연, 월, 일 가져오기
    var currentYear = thisMonth.getFullYear();
    var currentMonth = thisMonth.getMonth() + 1;
    var currentDate = thisMonth.getDate();
    dateSelect = document.querySelector('#date-select');
    dateSelect.innerHTML = '';
    for(var month = currentMonth; month <= 12; month++) {
        var date = currentYear + '-' + zeroFormat(month) + '-';
        var lastDay = new Date(currentYear, month, 0).getDate();
        for(var day = 1; day <= lastDay; day++) {
            dateSelect.innerHTML += `<option value="`+ date + zeroFormat(day) + `">`+ date + zeroFormat(day) + `</option>`;
        }
    }
}

let memberId = 0;
function getMemberId() {
    $.ajax({
        url : `/v1/member`,
        type : 'get',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        success : function(response) {
            memberId = response.data.memberId;
        },
        error : function(response, status, error) {
            alert(JSON.parse(response.responseText).message);
        }
    })
}

function redirectMemberId(path) {
    location.replace(dns + path + memberId);
}

function redirectUrl(path) {
    location.replace(dns + path);
}

function goPrev() {
    currentMonth -= 1;
    if (currentMonth == 0) {
        currentYear -= 1;
        currentMonth = 12;
    }

    renderCategoryPage(currentYear, currentMonth);
}

function goNext() {
    currentMonth += 1;
    if (currentMonth == 13) {
        currentYear += 1;
        currentMonth = 1;
    }

    renderCategoryPage(currentYear, currentMonth);
}


function zeroFormat(value) {
    if (value / 10 < 1) {
        return '0' + value;
    } else {
        return value;
    }
}


function selectCategory() {
    // 선택이 된 라디오 버튼의 value 가져오기
    const selectedCategory = document.querySelector('input[type=radio][name="categoryRadio"]:checked');
    const categoryName = selectedCategory.value;

    // 가져온 카테고리 value 를 버튼 위에 뒤집어 씌우기
    let categoryRadio = document.getElementById('category-radio');
    categoryRadio.setAttribute('value', selectedCategory.id);
    categoryRadio.innerHTML = categoryName;
}


var categoryCheck = 0; // 카테고리 리스트 중복 방지용
function getCategoryList() {
    $.ajax({
        url : `/v1/category/list`,
        type : 'get',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        success : function(response) {
            var data = response.data;

            if (newCategory > 0 || categoryCheck == 0) {
                modalContent = document.querySelector('.modal-body');
                var content = ``;
                for(var i = 0; i < data.length; i++) {
                    var categoryName = data[i].categoryName;
                    var categoryId = data[i].categoryId;

                    if (categoryName === '미분류') continue;

                    content += `<div class="form-check">
                                  <input class="form-check-input" type="radio" name="categoryRadio" id="`+ categoryId + `" value="`+ categoryName + `">
                                  <label class="form-check-label" for="`+ categoryId + `">` + categoryName + `</label>
                                  <button class="btn btn-success" onclick="getCategoryName(`+ categoryId + `);" data-bs-target="#modifyCategoryModal" data-bs-toggle="modal" data-bs-dismiss="modal">수정</button>
                                </div>`;
                }
                modalContent.innerHTML = content;
                categoryCheck++;
            }
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

var newCategory = 0;
function createCategory() {
    const params = {
        'categoryName' : $('#category-name').val()
    };

    $.ajax({
        url : `/v1/category`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('새로운 카테고리가 추가되었습니다.');
            document.getElementById('category-name').value = null;
            newCategory++;
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function getCategoryName(categoryId) {
    $.ajax({
        url : `/v1/category/` + categoryId,
        type : 'get',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        success : function(response) {
            const data = response.data;
            const editCategory = document.querySelector('.edit-category');

            editCategory.value = data.categoryName;
            editCategory.id = data.categoryId;
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function modifyCategory(categoryId) {
    const editCategory = document.querySelector('.edit-category');
    const params = {
        'categoryId' : editCategory.id,
        'categoryName' : editCategory.value
    };

    $.ajax({
        url : `/v1/category`,
        type : 'put',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('카테고리 수정이 완료되었습니다.');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function deleteCategory() {
    var categoryId = document.querySelector('.edit-category').id;

    $.ajax({
        url : `/v1/category/` + categoryId,
        type : 'delete',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        success : function(response) {
            alert('카테고리 삭제가 완료되었습니다.');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function createDailyPayments() {
    const date = document.getElementById('date-select');
    const params = {
        'paidAmount': $('#paid-amount').val(),
        'payLocation': $('#pay-location').val(),
        'methodOfPayment' : $('#method-payment').val(),
        'categoryId': $('#category-radio').val(),
        'memo': $('#memo').val(),
        'date' : date.options[date.selectedIndex].value
    };

    $.ajax({
        url : `/v1/daily-payments`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('지출 입력이 성공적으로 되었습니다');
            location.replace('http://www.localhost:8080/index');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function modifyPayment() {
    const date = document.getElementById('date-select');
    const params = {
        'paymentId' : window.location.pathname.split('/')[2],
        'paidAmount' : $('#paid-amount').val(),
        'payLocation' : $('#pay-location').val(),
        'methodOfPayment' : $('#method-payment').val(),
        'categoryId' : $('#category-radio').val(),
        'memo': $('#memo').val(),
        'date' : date.options[date.selectedIndex].value
    };

    $.ajax({
        url : `/v1/daily-payments`,
        type : 'put',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            redirectMemberId('/my-accountbook/');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function signIn() {
    var email = $('#email').val();
    var password = $('#password').val();
    const params = {
        'email' : email,
        'password' : password
    }

    $.ajax({
        url : `/v1/auth/sign-in`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('로그인 되었습니다.');
            redirectUrl('/index');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

var count = 0;
function sendAuthEmail() {
    var email = $('#email').val();
    const params = {
        'email' : email
    }

    $.ajax({
        url : `/v1/auth/email/send`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('인증 이메일이 전송되었습니다.');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })

    if (count === 0) {
        const emailButton = document.getElementById('emailButton');
        const newP = document.createElement('p');
        newP.innerHTML = "<br> <label for='authKey' class='form-label'>인증번호 입력</label> <div class='input-group'> <input id='authKey' type='text' class='form-control' placeholder='인증번호를 입력하세요'> <button type='button' class='btn btn-success' onclick='completeAuthEmail();'>인증</button> </div>";
        emailButton.appendChild(newP);

        count++;
    }
}

function completeAuthEmail() {
    var email = $('#email').val();
    var authKey = $('#authKey').val();
    const params = {
        'email' : email,
        'authKey' : authKey
    };

    $.ajax({
        url : `/v1/auth/email/complete`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('인증이 완료 되었습니다.');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}

function signUp() {
    var memberName = $('#memberName').val();
    var email = $('#email').val();
    var password = $('#password').val();
    const params = {
        'memberName' : memberName,
        'email' : email,
        'password' : password
    }

    $.ajax({
        url : `/v1/auth/sign-up`,
        type : 'post',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('회원가입이 완료 되었습니다.');
            redirectUrl('/index');
        },
        error : function(request, status, error) {
            alert(JSON.parse(request.responseText).message);
        }
    })
}


function modifyMemberInfo() {
    const params = {
        memberId : memberId,
        memberName : $('#memberName').val(),
        monthlyBudget : $('#monthlyBudget').val()
    };

    $.ajax({
        url : `/v1/member`,
        type : 'put',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('회원정보 수정이 완료 되었습니다.');
            redirectMemberId('/my-accountbook/');
        },
        error : function(response, status, error) {
            alert(JSON.parse(response.responseText).message);
        }
    })
}

function modifyPassword() {
    const params = {
        memberId : memberId,
        beforePassword : $('#beforePassword').val(),
        afterPassword : $('#afterPassword').val()
    };

    $.ajax({
        url : `/v1/member/password`,
        type : 'patch',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('비밀번호 수정이 완료 되었습니다.');
            redirectMemberId('/my-page/');
        },
        error : function(response, status, error) {
            alert(JSON.parse(response.responseText).message);
        }
    })
}

function disableMember() {
    const params = {
        memberId : memberId
    };

    $.ajax({
        url : `/v1/member`,
        type : 'delete',
        contentType : 'application/json; charset=utf-8;',
        dataType : 'json',
        data : JSON.stringify(params),
        success : function(response) {
            alert('회원탈퇴가 완료되었습니다.');
            window.location.href = "/logout";
            redirectUrl('/index');
        },
        error : function(response, status, error) {
            alert(JSON.parse(response.responseText).message);
        }
    })
}



