const express = require('express'),
  path = require('path'), // add path module
  app = express(),
  mysql = require('mysql'), // import mysql module
  cors = require('cors'),
  bodyParser = require('body-parser');

// setting up db
db = mysql.createConnection({
  host: 'localhost',
  user: process.env.USER,
  password: process.env.DB_PASS,
  database: 'trivia_duel_project'
})

// make server object containing port #
var server = {
  port: 8082
};

// routers
const qRouter = require('./routes/questions');

// use modules
app.use(cors())
app.use(bodyParser.json());
app.use(express.json());
app.use(express.urlencoded({extended: true}))

app.use((req,res,next) => {
  if (req.query.k != process.env.API_KEY){
    console.log(`${Math.floor(Date.now() / 1000)} | KEY [${req.query.k}] ATTEMPTED -- FAILURE`)
    res.status(401).json({error: 'unauthorized'})
  } else {
    console.log(`${Math.floor(Date.now() / 1000)} | KEY [${req.query.k}] ATTEMPTED -- SUCCESS`)
    next()
  }
})
// use router(s)
app.use('/questions', qRouter);

// router user input
app.get('/', function(req, res) {
  res.sendFile(path.resolve(__dirname, 'views') + '/input.html');
});

app.listen(server.port, () => console.log(`Server started, listening port: ${server.port}`));
