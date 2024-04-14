rootProject.name = "spring-kotlin-template"

include("backend:todo-web:app")
include("backend:todo-web:web")
include("backend:todo-background:app")
include("backend:todo-common:domain")
include("backend:todo-common:dataaccess:mongo")
include("backend:todo-common:dataaccess:http")
include("backend:todo-common:dataaccess:cache")
include("backend:todo-common:util:logging")
include("backend:todo-common:util:test")
