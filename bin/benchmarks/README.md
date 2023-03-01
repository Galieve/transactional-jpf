[`Back to init`](../../README.md)

## TrJPF Output

For each client and database configuration TrJPF output a file containing, among others, the following values:

- program under test,
- elapsed time,
- visited, backtracked and end states,
- number of final histories,
- max memory reserved (min 256 MB).

The system always execute EXPLORE(I_0, I) algorithm, where if I_0 = I, it behaves as EXPLORE(I). In particular, end states and number of final histories coincide when the isolation levels I_0 and I. Otherwise, the number of total histories explored is outputted as "end states" while the actual number of consistent histories is "transactional histories"