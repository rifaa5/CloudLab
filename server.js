//RESTAPI
//npm init -y
//npm install express
//node server.js

const express = require('express');
const app = express();

app.use(express.json());


let students = [
    { id: 1, name: 'Arun', dept: 'IT' },
    { id: 2, name: 'Priya', dept: 'CSE' }
];


app.get('/students', (req, res) => {
    res.json(students);
});

app.get('/students/:id', (req, res) => {
    const student = students.find(s => s.id === Number(req.params.id));

    if (!student)
        return res.status(404).json({ message: 'Student not found' });

    res.json(student);
});

app.post('/students', (req, res) => {
    const { id, name, dept } = req.body;

    if (id == null || !name || !dept)
        return res.status(400).json({ message: 'id, name and dept are required' });

    if (students.some(s => s.id === Number(id)))
        return res.status(409).json({ message: 'Student ID already exists' });

    const student = { id: Number(id), name, dept };
    students.push(student);

    res.status(201).json(student);
});

app.put('/students/:id', (req, res) => {
    const student = students.find(s => s.id === Number(req.params.id));

    if (!student)
        return res.status(404).json({ message: 'Student not found' });

    const { name, dept } = req.body;

    if (!name || !dept)
        return res.status(400).json({ message: 'name and dept are required' });

    student.name = name;
    student.dept = dept;

    res.json(student);
});

app.delete('/students/:id', (req, res) => {
    const index = students.findIndex(s => s.id === Number(req.params.id));

    if (index === -1)
        return res.status(404).json({ message: 'Student not found' });

    students.splice(index, 1);
    res.json({ message: 'Student deleted successfully' });
});


app.listen(3000, () => {
    console.log('Server running at http://localhost:3000');
});
