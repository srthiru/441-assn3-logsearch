import json
import urllib.parse
import boto3

print('Loading function')

s3 = boto3.client('s3')

def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))
    hashfileBucket = "tbucket-441"
    hashfileKey = "assn3/hashValues.txt"

    # Get the object from the event and show its content type
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'], encoding='utf-8')
    try:
        print("Uploaded File: " + key)
        
        hashFile = s3.get_object(Bucket=hashfileBucket, Key=hashfileKey)
        print(hashFile)
        
        content = hashFile['Body'].read().decode('utf-8').split("\n")
        interval = "".join(key.split("/")[2].split(".")[1].split("-"))
        
        print(content)
        print("Adding lookup for interval: " + interval)
        content.append(interval+": "+key)
        
        s3.put_object(Body="\n".join(content).encode('utf-8'), Bucket=hashfileBucket, Key=hashfileKey)
        
        return "done"
        
    except Exception as e:
        print(e)
        raise e

asdas
