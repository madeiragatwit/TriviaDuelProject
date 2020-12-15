const express = require('express'),
  router = express.Router();

function numToId(num) {
  switch (num) {
    case 1:
      return 'ansA';
      break;
    case 2:
      return 'ansB';
      break;
    case 3:
      return 'ansC';
      break;
    default:
      return 'ansD';
      break;
  }
}
function shuffle(data) {
    for (var i = 0; i < 4; i++) {
        var j = i + Math.floor(Math.random() * (4 - i));
        var temp = data[numToId(j)];
        data[numToId(j)] = data[numToId(i)];
        data[numToId(i)] = temp;
    }
    return data;
}

router.get('/list', function(req,res) {
  let sql = `SELECT * FROM questions_ez`;
  db.query(sql, function(err, data, fields) {
    if (err) throw err;
    res.json({
      status: 200,
      data,
      message: "Question list retrieved successfully"
    })
  })
});

router.get('/rnd', function(req,res) {
  let sql = `SELECT * FROM questions_ez ORDER BY RAND() LIMIT 1`;
  db.query(sql, function(err, data, fields) {
    if (err) throw err;
    data = JSON.parse(JSON.stringify(data[0]))
    let correctAns = data['ansA'];
    let data2 = shuffle(data);
    data2['correctAns'] = correctAns;
    res.json({
      status: 200,
      data2,
      message: "Random question retrieved successfully"
    })
  })
})

router.post('/new', function(req,res) {
  let sql = `INSERT INTO questions_ez(QUESTION,ansA,ansB,ansC,ansD) VALUES (?)`;
  let values = [
    req.body.question,
    req.body.ansA,
    req.body.ansB,
    req.body.ansC,
    req.body.ansD
  ];
  db.query(sql, [values], function(err, data, fields) {
    if (err) throw err;
    res.json({
      status: 200,
      message: "New question added successfully"
    })
  })
});

router.post('/remove', function(req,res) {
  let sql = `DELETE FROM questions_ez WHERE ID = ${req.body.id}`;
  db.query(sql, function(err, data, fields) {
    if (err) throw err;
    res.json({
      status: 200,
      message: "Question deleted successfully"
    })
  })
})
module.exports = router;
