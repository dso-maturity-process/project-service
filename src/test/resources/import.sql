insert into project(id, name, description) values(10001, 'VA-PARS', 'Provides reporting of project status.');
insert into project(id, name, description) values(10002, 'IAM', 'Provides identity and access management support to a multitude of products.');

insert into user(id, firstname, lastName, userName, password) values(10002, 'william', 'drew', 'wdrew@governmentcio.com', 'password');

insert into project_user(id, user_id, project_id) values(10001, 10002, 10001);
