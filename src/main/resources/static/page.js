$(async function() {
    await getTableWithUsers();
    getDefaultModal();
    getActiveUserInfo();
    addNewUser();
    getNewUserForm()
})


const userFetchService = {
    head: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Referer': null
    },
    findAllUsers: async () => await fetch('api'),
    addNewUser: async (user) => await fetch('api', {
        method: 'POST',
        headers: userFetchService.head,
        body: JSON.stringify(user)
    }),
    findOneUser: async (id) => await fetch(`api/${id}`),
    getPrincipalInfo: async () => await fetch(`api/principal`),
    updateUser: async (user, id) => await fetch(`api/${id}`, {
        method: 'PUT',
        headers: userFetchService.head,
        body: JSON.stringify(user)
    }),
    deleteUser: async (id) => await fetch(`api/${id}`, {method: 'DELETE', headers: userFetchService.head})

}

async function getActiveUserInfo() {
    let headInfo = $('#headInfo')

    let principal = await userFetchService.getPrincipalInfo();
    let user = principal.json();
    user.then(user => {
        let userInfoFilling = `
       <h6> <b> ${user.username}</b> with roles: ${user.rolesView} </h6>
    `
        headInfo.append(userInfoFilling)
    })
}


async function getTableWithUsers() {
    let table = $('#tableUsers tbody');
    table.empty();

    await userFetchService.findAllUsers()
        .then(res => res.json())
        .then(users => {
            users.forEach(user => {
                let tableFilling = `$(
                       <tr>
                            <td style='text-align: center'>${user.id}</td>
                            <td style='text-align: center'>${user.username}</td>
                            <td style='text-align: center'>${user.userAge}</td>
                            <td style='text-align: center'>${user.userJob}</td>
                            <td style='text-align: center'>${user.rolesView}</td>
                            <td style='text-align: center'>
                                <button type="button" data-userid="${user.id}" data-action="edit" class="btn btn-info"
                                data-toggle="modal" data-target="#someDefaultModal">Edit</button>
                            </td>
                            <td style='text-align: center'>
                                <button type="button" data-userid="${user.id}" data-action="delete" class="btn btn-danger"
                                data-toggle="modal" data-target="#someDefaultModal">Delete</button>
                            </td>
                        </tr> 
                )`;
                table.append(tableFilling);
            })
        })

    $("#tableUsers").find('button').on('click', (event) => {
        let defaultModal = $('#someDefaultModal');
        let targetButton = $(event.target);
        let buttonUserId = targetButton.attr('data-userid');
        let buttonAction = targetButton.attr('data-action');

        defaultModal.attr('data-userid', buttonUserId);
        defaultModal.attr('data-action', buttonAction);
        defaultModal.modal('show');
    })
}


async function editUser(modal, id) {
    let preuser = await userFetchService.findOneUser(id);
    let user = preuser.json();

    modal.find('.modal-title').html('Edit user');

    let editButton = `<button  class="btn btn-primary" id="editButton">Edit</button>`;
    let closeButton = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(editButton);
    modal.find('.modal-footer').append(closeButton);

    user.then(user => {
        let bodyForm = `
            <div align="center">
            <form class="form-group" id="editUser" >
            <div class="col-7">
                <strong><labelfor="id">ID</label></strong>
                <input type="text" class="form-control" id="id" name="id" value="${user.id}" disabled><br>
                <strong><labelfor="username">Username</label></strong>
                <input class="form-control" type="text" id="username" value="${user.username}"><br>
                <strong><labelfor="userAge">Age</label></strong>
                <input class="form-control" id="userAge" min="1" onkeyup="if(value<0) value =0" type="number" value="${user.userAge}"> <br>
                <strong><labelfor="userJob">Job</label></strong>
                <input class="form-control" type="text" id="userJob" value="${user.userJob}" ><br>
                <strong><labelfor="password">Password</label></strong>
                <input class="form-control" type="password" id="password" value="${user.password}" disabled><br>
                <strong><labelfor="roles">Roles</label></strong>
                <select class="custom-select"
                        size="3"
                        multiple name="roles"
                        id="roles" required>
                <option value="ROLE_ADMIN">ADMIN</option>
                <option selected value="ROLE_USER">USER</option>
                </select>
                </div>
            </form>
            </div>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#editButton").on('click', async () => {
        let id = modal.find("#id").val().trim();
        let username = modal.find("#username").val().trim();
        let userAge = modal.find("#userAge").val().trim();
        let userJob = modal.find("#userJob").val().trim();
        let password = modal.find("#password").val().trim();
        let idRoles = 0
        let editRolesList = [];
        for (let i = 0; i < $('#roles').val().length; i++) {
            if ($('#roles').val()[i] === 'ROLE_ADMIN') {
                idRoles = 1
            } else {
                idRoles = 2
            }
            editRolesList[i] = {id: idRoles, name: $('#roles').val()[i]};
        }

        let data = {
            id: id,
            username: username,
            userAge: userAge,
            userJob: userJob,
            password: password,
            roles: editRolesList

        }
        const response = await userFetchService.updateUser(data, id);

        if (response.ok) {
            getTableWithUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12"
                            role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}


async function deleteUser(modal, id) {
    let preUser = await userFetchService.findOneUser(id);
    let user = preUser.json();

    modal.find('.modal-title').html('Delete User');

    let deleteButton = `<button  class="btn btn-danger" id="deleteButton">Delete</button>`;
    let closeButtonDelete = `<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>`
    modal.find('.modal-footer').append(deleteButton);
    modal.find('.modal-footer').append(closeButtonDelete);

    user.then(user => {
        let bodyForm = `
            <div align="center">
            <form class="form-group" id="deleteUser" >
            <div class="col-7">
                <strong><labelfor="id">ID</label></strong>
                <input type="text" class="form-control" id="id" name="id" value="${user.id}" disabled><br>
                <strong><labelfor="username">Username</label></strong>
                <input class="form-control" type="text" id="username" value="${user.username} "disabled><br>
                <strong><labelfor="userAge">Age</label></strong>
                <input class="form-control" type="text" id="userAge" value="${user.userAge} "disabled><br>
                <strong><labelfor="userJob">Job</label></strong>
                <input class="form-control" type="text" id="userJob" value="${user.userJob} "disabled><br>
                <strong><labelfor="password">Password</label></strong>
                <input class="form-control" type="password" id="password" value="${user.password} "disabled><br>
                <strong><labelfor="roles">Roles</label></strong>
                <select class="custom-select"
                        size="3"
                        multiple name="roles"
                        id="roles" required disabled>
                <option value="1">ADMIN</option>
                <option value="2">USER</option>
                </select>
                </div>
            </form>
            </div>
        `;
        modal.find('.modal-body').append(bodyForm);
    })

    $("#deleteButton").on('click', async () => {
        let id = modal.find("#id").val().trim();

        const response = await userFetchService.deleteUser(id);

        if (response.ok) {
            getTableWithUsers();
            modal.modal('hide');
        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            modal.find('.modal-body').prepend(alert);
        }
    })
}

async function getDefaultModal() {
    $('#someDefaultModal').modal({
        keyboard: true,
        backdrop: "static",
        show: false
    }).on("show.bs.modal", (event) => {
        let thisModal = $(event.target);
        let userid = thisModal.attr('data-userid');
        let action = thisModal.attr('data-action');
        switch (action) {
            case 'edit':
                editUser(thisModal, userid);
                break;
            case 'delete':
                deleteUser(thisModal, userid);
                break;
        }
    }).on("hidden.bs.modal", (e) => {
        let thisModal = $(e.target);
        thisModal.find('.modal-title').html('');
        thisModal.find('.modal-body').html('');
        thisModal.find('.modal-footer').html('');
    })
}


async function getNewUserForm() {
    let button = $(`#SliderNewUserForm`);
    let form = $(`#defaultSomeForm`)
    button.on('click', () => {
        if (form.attr("data-hidden") === "true") {
            form.attr('data-hidden', 'false');
            form.show();
            button.text('New User');
        } else {
            form.attr('data-hidden', 'true');
            form.hide();
            button.text('New User');
        }
    })
}


async function addNewUser() {
    $('#addNewUserButton').click(async () =>  {
        let addUserForm = $('#defaultSomeForm')
        let username = addUserForm.find('#AddNewUserName').val().trim();
        let userAge = addUserForm.find('#AddNewUserAge').val().trim();
        let userJob = addUserForm.find('#AddNewUserJob').val().trim();
        let password = addUserForm.find('#AddNewUserPassword').val().trim();
        let id = 0
        let rolesList = [];
        for (let i = 0; i < $('#AddNewUserRoles').val().length; i++) {
            if ($('#AddNewUserRoles').val()[i] === '1') {
                id = 1
            } else {
                id = 2
            }
            rolesList[i] = {id: id, role: $('#AddNewUserRoles').val()[i]};
        }

        let data = {
            username: username,
            userAge: userAge,
            userJob: userJob,
            password: password,
            roles: rolesList
        }

        const response = await userFetchService.addNewUser(data);
        if (response.ok) {
            getTableWithUsers();
            addUserForm.find('#AddNewUserUsername').val('');
            addUserForm.find('#AddNewUserAge').val('');
            addUserForm.find('#AddNewUserJob').val('');
            addUserForm.find('#AddNewUserPassword').val('');
            addUserForm.find('#AddNewUserRoles').val('');

        } else {
            let body = await response.json();
            let alert = `<div class="alert alert-danger alert-dismissible fade show col-12" role="alert" id="sharaBaraMessageError">
                            ${body.info}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`;
            addUserForm.prepend(alert)
        }
    })
}
