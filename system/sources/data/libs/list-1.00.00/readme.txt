El request completo para una lista puede ser:

    GET /api/v1/patients?sort=+name,-lastName&fields=name,lastName&filter=+esteban,-cabezudo&offset=10&limit=50


La fuente de datos debe entregar en una llamada sin parámetros lo siguiente.

{
  "filters": "strings to filter"
  "headers":[
     {
       "title":"Id"
     },
     {
       "title":"Nombre de la ciudad"
     }
  ],
  "totalRecords":12673,
  "pageSize":200
}

Es una lista de objetos porque se espera poder colocar otros valores como el ancho esperado de la columna o si se tiene que mostrar o no

Si se especifica un parámetro s, debe entregar una lista de objetos con la siguiente estructura

{
  "list":[
    {
      "row":"5",
      "fields":[
        "6",
        "Andorra"
      ]
    }
  ]
}