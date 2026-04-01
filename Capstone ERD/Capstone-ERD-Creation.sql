{
    "type": "MySQLNotebook",
    "version": "1.0",
    "caption": "DB Notebook",
    "content": "\n\nCREATE DATABASE Capstone_IMPROVED;\nuse Capstone_IMPROVED;\nCREATE TABLE events (\n    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    event_name VARCHAR(255) NOT NULL,\n    date DATE NOT NULL,\n    status ENUM('SCHEDULED','CANCELLED','COMPLETED') NOT NULL,\n    total_spots INT NOT NULL,\n    venue_id INT NOT NULL\n);\n\nALTER TABLE events\nADD UNIQUE events_venue_id_unique (venue_id);\n\nCREATE TABLE venues (\n    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    venue_name VARCHAR(255) NOT NULL,\n    location VARCHAR(255) NOT NULL,\n    total_capacity BIGINT NOT NULL\n);\n\nCREATE TABLE tickets (\n    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    event_id INT NOT NULL,\n    price DECIMAL(10,2) NOT NULL,\n    total_quantity INT NOT NULL,\n    sold INT NOT NULL\n);\n\nALTER TABLE tickets\nADD UNIQUE tickets_event_id_unique (event_id);\n\nCREATE TABLE users (\n    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    username VARCHAR(255) NOT NULL,\n    password VARCHAR(255) NOT NULL,\n    role ENUM('ADMIN', 'USER')\n);\n\nCREATE TABLE tickets_sold (\n    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n    user_id INT NOT NULL,\n    ticket_id INT NOT NULL,\n    date_sold DATE NOT NULL\n);\n\nALTER TABLE events\nADD CONSTRAINT events_venue_id_foreign\nFOREIGN KEY (venue_id) REFERENCES venues (id);\n\nALTER TABLE tickets\nADD CONSTRAINT tickets_event_id_foreign\nFOREIGN KEY (event_id) REFERENCES events (id);\n\nALTER TABLE tickets_sold\nADD CONSTRAINT tickets_sold_user_id_foreign\nFOREIGN KEY (user_id) REFERENCES users (id);\n\nALTER TABLE tickets_sold\nADD CONSTRAINT tickets_sold_ticket_id_foreign\nFOREIGN KEY (ticket_id) REFERENCES tickets (id);\n",
    "options": {
        "tabSize": 4,
        "insertSpaces": true,
        "indentSize": 4,
        "defaultEOL": "CRLF",
        "trimAutoWhitespace": true
    },
    "viewState": {
        "cursorState": [
            {
                "inSelectionMode": false,
                "selectionStart": {
                    "lineNumber": 48,
                    "column": 1
                },
                "position": {
                    "lineNumber": 48,
                    "column": 1
                }
            }
        ],
        "viewState": {
            "scrollLeft": 0,
            "firstPosition": {
                "lineNumber": 28,
                "column": 1
            },
            "firstPositionDeltaTop": -12
        },
        "contributionsState": {
            "editor.contrib.folding": {},
            "editor.contrib.wordHighlighter": false
        }
    },
    "contexts": [
        {
            "state": {
                "start": 1,
                "end": 63,
                "language": "mysql",
                "result": {
                    "type": "text",
                    "text": [
                        {
                            "type": 2,
                            "index": 0,
                            "resultId": "a6f24995-ef0d-411e-8c93-bc68a1349b32",
                            "content": "OK, 1 row affected in 8.894ms"
                        }
                    ]
                },
                "currentHeight": 28,
                "currentSet": 1,
                "statements": [
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 0,
                            "length": 36
                        },
                        "contentStart": 2,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 36,
                            "length": 23
                        },
                        "contentStart": 37,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 59,
                            "length": 254
                        },
                        "contentStart": 60,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 313,
                            "length": 66
                        },
                        "contentStart": 315,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 379,
                            "length": 183
                        },
                        "contentStart": 381,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 562,
                            "length": 191
                        },
                        "contentStart": 564,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 753,
                            "length": 68
                        },
                        "contentStart": 755,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 821,
                            "length": 176
                        },
                        "contentStart": 823,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 997,
                            "length": 162
                        },
                        "contentStart": 999,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 1159,
                            "length": 106
                        },
                        "contentStart": 1161,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 1265,
                            "length": 108
                        },
                        "contentStart": 1267,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 1373,
                            "length": 115
                        },
                        "contentStart": 1375,
                        "state": 0
                    },
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 1488,
                            "length": 121
                        },
                        "contentStart": 1490,
                        "state": 0
                    }
                ]
            },
            "data": []
        },
        {
            "state": {
                "start": 64,
                "end": 64,
                "language": "mysql",
                "currentSet": 1,
                "statements": [
                    {
                        "delimiter": ";",
                        "span": {
                            "start": 0,
                            "length": 0
                        },
                        "contentStart": 0,
                        "state": 3
                    }
                ]
            },
            "data": []
        }
    ]
}