(: xquery version "1.0-ml"; :)

(: Retrieves latest entries to the collection :)
for $doc in fn:collection("team27/proposals")[1 to 3]
let $uri := xdmp:node-uri($doc)
let $updated-date := xdmp:document-get-properties($uri, fn:QName("http://marklogic.com/cpf", "last-updated"))
order by $updated-date/text()
return $doc