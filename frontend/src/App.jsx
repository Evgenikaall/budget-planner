import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Container,
  TextField,
  Box,
  Paper,
  List,
  ListItem,
  ListItemText,
  Stack,
  Tabs,
  Tab,
  MenuItem,
  Select,
  InputLabel,
  FormControl
} from '@mui/material';

const api = axios.create({
  baseURL: '/api/v1'
});

function Auth({ onAuthenticated }) {
  const [mode, setMode] = useState('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (mode === 'register') {
        await api.post('/auth/register', { username, password, roles: ['ROLE_USER'] });
        setMode('login');
      } else {
        const { data } = await api.post('/auth/login', { username, password });
        localStorage.setItem('token', data.token);
        onAuthenticated();
      }
    } catch (err) {
      setError(err.response?.data || err.message);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper sx={{ p: 4 }} elevation={3}>
        <Typography variant="h5" component="h2" gutterBottom>
          {mode === 'login' ? 'Login' : 'Register'}
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            fullWidth
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            fullWidth
          />
          {error && (
            <Typography color="error" variant="body2">
              {String(error)}
            </Typography>
          )}
          <Button type="submit" variant="contained" color="primary">
            {mode === 'login' ? 'Login' : 'Register'}
          </Button>
        </Box>
        <Button sx={{ mt: 2 }} onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
          Switch to {mode === 'login' ? 'Register' : 'Login'}
        </Button>
      </Paper>
    </Container>
  );
}

function Budgets() {
  const [budgets, setBudgets] = useState([]);
  const [name, setName] = useState('');
  const [limitAmount, setLimitAmount] = useState('');

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) return;
    api
      .get('/budgets', { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => setBudgets(res.data))
      .catch((err) => console.error(err));
  }, [token]);

  const createBudget = async (e) => {
    e.preventDefault();
    try {
      const { data } = await api.post(
        '/budgets',
        { name, limitAmount: Number(limitAmount) },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setBudgets((prev) => [...prev, data]);
      setName('');
      setLimitAmount('');
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Paper sx={{ p: 3, mt: 3 }} elevation={1}>
      <Typography variant="h6" gutterBottom>
        Budgets
      </Typography>
      <List dense>
        {budgets.map((b) => (
          <ListItem key={b.id} divider>
            <ListItemText primary={b.name} secondary={`Limit: ${b.limitAmount}`} />
          </ListItem>
        ))}
      </List>
      <Box component="form" onSubmit={createBudget} sx={{ mt: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
          />
          <TextField
            label="Limit"
            value={limitAmount}
            onChange={(e) => setLimitAmount(e.target.value)}
            fullWidth
          />
          <Button type="submit" variant="contained">
            Add
          </Button>
        </Stack>
      </Box>
    </Paper>
  );
}

function Categories({ token }) {
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState('');
  const [color, setColor] = useState('');

  useEffect(() => {
    if (!token) return;
    api
      .get('/categories', { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => setCategories(res.data))
      .catch((err) => console.error(err));
  }, [token]);

  const createCategory = async (e) => {
    e.preventDefault();
    try {
      const { data } = await api.post(
        '/categories',
        { name, color },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setCategories((prev) => [...prev, data]);
      setName('');
      setColor('');
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Paper sx={{ p: 3, mt: 3 }} elevation={1}>
      <Typography variant="h6" gutterBottom>
        Categories
      </Typography>
      <List dense>
        {categories.map((c) => (
          <ListItem key={c.id} divider>
            <ListItemText primary={c.name} secondary={c.color} />
          </ListItem>
        ))}
      </List>
      <Box component="form" onSubmit={createCategory} sx={{ mt: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            fullWidth
          />
          <TextField
            label="Color"
            value={color}
            onChange={(e) => setColor(e.target.value)}
            fullWidth
          />
          <Button type="submit" variant="contained">
            Add
          </Button>
        </Stack>
      </Box>
    </Paper>
  );
}

function Expenses({ token }) {
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState('');
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [date, setDate] = useState('');
  const [filterCategoryId, setFilterCategoryId] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    if (!token) return;
    api
      .get('/categories', { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => setCategories(res.data))
      .catch((err) => console.error(err));
  }, [token]);

  const loadExpenses = () => {
    if (!token) return;
    const params = {};
    if (filterCategoryId) params.categoryId = filterCategoryId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    api
      .get('/expenses', {
        headers: { Authorization: `Bearer ${token}` },
        params
      })
      .then((res) => setExpenses(res.data))
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    loadExpenses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const createExpense = async (e) => {
    e.preventDefault();
    try {
      const isoDate = date ? new Date(date).toISOString() : new Date().toISOString();
      const { data } = await api.post(
        '/expenses',
        { description, amount: Number(amount), date: isoDate, categoryId },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setExpenses((prev) => [...prev, data]);
      setDescription('');
      setAmount('');
      setDate('');
      setCategoryId('');
    } catch (err) {
      console.error(err);
    }
  };

  const deleteExpense = async (id) => {
    try {
      await api.delete(`/expenses/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setExpenses((prev) => prev.filter((e) => e.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Paper sx={{ p: 3, mt: 3 }} elevation={1}>
      <Typography variant="h6" gutterBottom>
        Expenses
      </Typography>

      <Box sx={{ mb: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <FormControl fullWidth>
            <InputLabel id="filter-category-label">Category</InputLabel>
            <Select
              labelId="filter-category-label"
              label="Category"
              value={filterCategoryId}
              onChange={(e) => setFilterCategoryId(e.target.value)}
            >
              <MenuItem value="">
                <em>All</em>
              </MenuItem>
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Start date"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            fullWidth
          />
          <TextField
            label="End date"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            fullWidth
          />
          <Button variant="outlined" onClick={loadExpenses}>
            Filter
          </Button>
        </Stack>
      </Box>

      <List dense>
        {expenses.map((e) => (
          <ListItem
            key={e.id}
            divider
            secondaryAction={
              <Button color="error" size="small" onClick={() => deleteExpense(e.id)}>
                Delete
              </Button>
            }
          >
            <ListItemText
              primary={`${e.description || 'Expense'} – ${e.amount}`}
              secondary={`${e.categoryName || ''} • ${new Date(e.date).toLocaleString()}`}
            />
          </ListItem>
        ))}
      </List>

      <Box component="form" onSubmit={createExpense} sx={{ mt: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            fullWidth
          />
          <TextField
            label="Amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            fullWidth
          />
          <TextField
            label="Date"
            type="datetime-local"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            fullWidth
          />
          <FormControl fullWidth>
            <InputLabel id="category-label">Category</InputLabel>
            <Select
              labelId="category-label"
              label="Category"
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
            >
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Button type="submit" variant="contained">
            Add
          </Button>
        </Stack>
      </Box>
    </Paper>
  );
}

function App() {
  const [authenticated, setAuthenticated] = useState(!!localStorage.getItem('token'));
  const [tab, setTab] = useState(0);

  const token = localStorage.getItem('token');

  const logout = () => {
    localStorage.removeItem('token');
    setAuthenticated(false);
  };

  if (!authenticated) {
    return <Auth onAuthenticated={() => setAuthenticated(true)} />;
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Budget Planner
          </Typography>
          <Button color="inherit" onClick={logout}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>
      <Container sx={{ mt: 4 }}>
        <Tabs value={tab} onChange={(_, value) => setTab(value)} sx={{ mb: 2 }}>
          <Tab label="Budgets" />
          <Tab label="Categories" />
          <Tab label="Expenses" />
        </Tabs>
        {tab === 0 && <Budgets />}
        {tab === 1 && <Categories token={token} />}
        {tab === 2 && <Expenses token={token} />}
      </Container>
    </Box>
  );
}

export default App;
