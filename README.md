# Run with docker
```
./start.sh
```
# Run for development:
### start db
```
sudo service postgresql start
```
### start backend
```
cd ./backend/backend/src
lein start
```
### run tests on backend
```
cd ./backend/backend/src
lein test
```
### start frontend
```
cd ./frontend
lein figwheel
```
### run tests on frontend
```
cd ./backend/backend/src

```