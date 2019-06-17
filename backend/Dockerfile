FROM python:3

COPY . /app/backend
WORKDIR /app/backend

RUN pip install -r requirements.txt
RUN mkdir -p /app/backend/upload

EXPOSE 5005

ENTRYPOINT ["python3"]
CMD ["./app.py"]