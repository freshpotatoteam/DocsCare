FROM python:3

RUN apt-get update -y
RUN apt-get install -y tesseract-ocr libtesseract-dev poppler-utils default-jre

COPY . /app/backend
WORKDIR /app/backend

RUN pip install -r requirements.txt
RUN mkdir -p /app/backend/upload

COPY ./tessdata /usr/share/tesseract-ocr/tessdata

EXPOSE 5005

ENTRYPOINT ["python3"]
CMD ["./app.py"]
