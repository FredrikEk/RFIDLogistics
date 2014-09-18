RFID Server
===========

Run
---

    $ play run

Run tests
---------

    $ play test

API examples
------------

### Pallets

Tests on the API, where you also can see how you use it:
[test/controllers/api/PalletViewTest.java](test/controllers/api/PalletViewTest.java)

List pallets:

    $ curl "http://localhost:9000/api/pallets"
    [{"tag1":"AAOBung5MG+/DzdEZXR6FQ","tag2":"vTedJCQtzgpfMGdUTNemIQ","time_entrance":"Mon Mar 03 00:00:00 CET 2014","time_left":"null","products":[{"article":"ICA0022","amount":20},{"article":"ICA001","amount":10}]}]

Add a pallet with products:

    $ curl "http://localhost:9000/api/pallets" -X POST -d '{"tag1":"blabla1","tag2":"blabla2","time_entrance":"2014-03-03 00:00:00","products":[{"article":"ICA001","amount":10},{"article":"ICA002","amount":20}]}' -H "Content-Type: application/json"

### Pallet slots

List pallet slots:

    $ curl "http://localhost:9000/api/palletslots"
    [{"position":"AB01234","tag":{"id":"blalbalbla"}},{"position":"AB01235","tag":{"id":"blalbaa123"}}]

Add a new pallet slot:

    $ curl "http://localhost:9000/api/palletslots" -X POST -d '{"position":"AB01235","tag":"blalbaa123"}' -H "Content-Type: application/json"

### Tags

List tags:

    $ curl "http://localhost:9000/api/tags"
    [{"id":"blabla5"},{"id":"blabla6"},{"id":"blalbaa123"},{"id":"blalbalbla"},{"id":"blalbalbla123"}]

### Moves

Tests on the API, where you also can see how you use it:
[test/controllers/api/MoveViewTest.java](test/controllers/api/MoveViewTest.java)

List moves:

    $ curl "http://localhost:9000/api/moves"
    [{"tag1":"palletTag1","tag2":"palletTag2","reader":"reader","date":"Mon Dec 10 12:12:12 CET 2012"}]

Create a move:

    $ curl "http://localhost:9000/api/moves" -X POST -d '{"reader":"Truck 1","date":"2014-01-01 10:10:10","tags":["tag1","tag2"]}' -H "Content-Type: application/json"

Create a move to `floor`:

    $ curl "http://localhost:9000/api/moves" -X POST -d '{"reader":"Truck 1","date":"2014-01-01 10:10:10","tags":["floor","tag1"]}' -H "Content-Type: application/json"
